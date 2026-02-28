package com.oda.infrastructure.job.external;

import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.job.Company;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;
import com.oda.domain.job.SkillRequirement;
import com.oda.domain.job.JobDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class SaraminApiAdapter implements JobDataSource {

    private final RestClient restClient;
    private final String apiKey;
    private final AtomicInteger dailyCallCount = new AtomicInteger(0);
    private static final int MAX_DAILY_CALLS = 480;
    static final String BASE_URL = "https://oapi.saramin.co.kr";

    public SaraminApiAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${oda.api.saramin-key:}") String apiKey) {
        this.restClient = restClientBuilder.baseUrl(BASE_URL).build();
        this.apiKey = apiKey;
    }

    // Constructor for testing with custom base URL
    SaraminApiAdapter(RestClient.Builder restClientBuilder, String apiKey, String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    @Override
    public List<JobPosting> fetchJobs() {
        if (dailyCallCount.incrementAndGet() > MAX_DAILY_CALLS) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Saramin daily limit exceeded");
        }

        try {
            SaraminJobResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/job-search")
                            .queryParam("access-key", apiKey)
                            .queryParam("count", 100)
                            .queryParam("fields", "posting-date,expiration-date,keyword-code,hit-cnt,paid-normal")
                            .build())
                    .retrieve()
                    .body(SaraminJobResponse.class);

            if (response == null || response.jobs() == null || response.jobs().job() == null) {
                return new ArrayList<>();
            }

            return response.jobs().job().stream()
                    .map(this::toJobPosting)
                    .toList();
        } catch (CoreException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch jobs from Saramin", e);
            return new ArrayList<>();
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resetDailyCount() {
        log.info("Resetting Saramin daily call count");
        dailyCallCount.set(0);
    }

    public int getDailyCallCount() {
        return dailyCallCount.get();
    }

    private JobPosting toJobPosting(SaraminJobResponse.Job job) {
        SaraminJobResponse.Position position = job.position();
        SaraminJobResponse.Company company = job.company();

        String companyName = company != null && company.detail() != null ? company.detail().name() : null;
        String industry = company != null && company.detail() != null ? company.detail().industry() : null;
        String companySize = company != null && company.detail() != null ? company.detail().size() : null;
        String companyLocation = company != null && company.detail() != null ? company.detail().location() : null;

        Company domainCompany = new Company(companyName, industry, companySize, companyLocation);

        JobPosting posting = JobPosting.create(
                position != null ? position.title() : "Unknown",
                domainCompany,
                JobSource.SARAMIN
        );

        String location = position != null && position.location() != null ? position.location().name() : null;
        String jobType = position != null && position.jobType() != null ? position.jobType().name() : null;
        Integer expYears = position != null && position.experienceLevel() != null
                ? position.experienceLevel().min() : null;
        String education = position != null && position.requiredEducationLevel() != null
                ? position.requiredEducationLevel().name() : null;
        String salary = job.salary() != null ? job.salary().name() : null;
        String description = position != null ? position.description() : null;

        LocalDate deadline = null;
        if (position != null && position.expirationDate() != null && !position.expirationDate().isBlank()) {
            try {
                deadline = LocalDate.parse(position.expirationDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                try {
                    deadline = LocalDate.parse(position.expirationDate().substring(0, 10));
                } catch (Exception ignored) {}
            }
        }

        List<SkillRequirement> skills = new ArrayList<>();
        if (position != null && position.jobMidCode() != null && position.jobMidCode().name() != null) {
            skills.add(new SkillRequirement(position.jobMidCode().name(), true, 1));
        }

        posting.update(job.id(), description, skills, expYears, education, salary, location, jobType,
                job.url(), deadline);

        return posting;
    }
}
