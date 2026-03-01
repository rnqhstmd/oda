package com.oda.infrastructure.job.external;

import com.oda.domain.job.Company;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;
import com.oda.domain.job.SkillRequirement;
import com.oda.domain.job.JobDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WantedApiAdapter implements JobDataSource {

    private final RestClient restClient;
    private final String apiKey;
    static final String BASE_URL = "https://api.wanted.co.kr";

    public WantedApiAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${oda.api.wanted-key:}") String apiKey) {
        this.restClient = restClientBuilder.baseUrl(BASE_URL).build();
        this.apiKey = apiKey;
    }

    // Constructor for testing with custom base URL
    WantedApiAdapter(RestClient.Builder restClientBuilder, String apiKey, String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    @Override
    public List<JobPosting> fetchJobs() {
        try {
            WantedJobResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v4/jobs")
                            .queryParam("country", "kr")
                            .queryParam("job_sort", "job.latest_order")
                            .queryParam("limit", 100)
                            .build())
                    .header("wanted_user_token", apiKey)
                    .retrieve()
                    .body(WantedJobResponse.class);

            if (response == null || response.data() == null) {
                return new ArrayList<>();
            }

            return response.data().stream()
                    .map(this::toJobPosting)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to fetch jobs from Wanted", e);
            return new ArrayList<>();
        }
    }

    private JobPosting toJobPosting(WantedJobResponse.Job job) {
        WantedJobResponse.Company company = job.company();
        String companyName = company != null ? company.name() : null;
        String industry = company != null ? company.industryName() : null;
        String companyType = company != null ? company.type() : null;
        String addressLocation = job.address() != null ? job.address().location() : null;

        Company domainCompany = new Company(companyName, industry, companyType, addressLocation);

        JobPosting posting = JobPosting.create(
                job.title() != null ? job.title() : "Unknown",
                domainCompany,
                JobSource.WANTED
        );

        List<SkillRequirement> skills = new ArrayList<>();
        if (job.skills() != null) {
            job.skills().forEach(skill -> skills.add(new SkillRequirement(skill, true, 1)));
        }

        LocalDate deadline = null;
        if (job.dueTime() != null && !job.dueTime().isBlank()) {
            try {
                deadline = LocalDate.parse(job.dueTime().substring(0, 10));
            } catch (Exception ignored) {}
        }

        String description = null;
        if (job.detail() != null) {
            StringBuilder sb = new StringBuilder();
            if (job.detail().intro() != null) sb.append(job.detail().intro()).append("\n");
            if (job.detail().mainTasks() != null) sb.append(job.detail().mainTasks()).append("\n");
            if (job.detail().requirements() != null) sb.append(job.detail().requirements());
            description = sb.toString();
        }

        posting.update(
                String.valueOf(job.id()),
                description,
                skills,
                job.experienceMin(),
                null,
                null,
                addressLocation,
                job.jobType(),
                job.url(),
                deadline
        );

        return posting;
    }
}
