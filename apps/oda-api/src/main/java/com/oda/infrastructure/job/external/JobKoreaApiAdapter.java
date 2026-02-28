package com.oda.infrastructure.job.external;

import com.oda.domain.job.Company;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;
import com.oda.domain.job.SkillRequirement;
import com.oda.domain.job.JobDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JobKoreaApiAdapter implements JobDataSource {

    private final RestClient restClient;
    static final String BASE_URL = "https://www.jobkorea.co.kr";
    private static final String JOBKOREA_JOB_URL_PREFIX = "https://www.jobkorea.co.kr/Recruit/GI_Read/";

    public JobKoreaApiAdapter(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl(BASE_URL).build();
    }

    // Constructor for testing with custom base URL
    JobKoreaApiAdapter(RestClient.Builder restClientBuilder, String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public List<JobPosting> fetchJobs() {
        try {
            JobKoreaJobResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/jobs")
                            .queryParam("count", 100)
                            .build())
                    .retrieve()
                    .body(JobKoreaJobResponse.class);

            if (response == null || response.jobs() == null) {
                return new ArrayList<>();
            }

            return response.jobs().stream()
                    .map(this::toJobPosting)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to fetch jobs from JobKorea", e);
            return new ArrayList<>();
        }
    }

    private JobPosting toJobPosting(JobKoreaJobResponse.Job job) {
        Company domainCompany = new Company(
                job.companyName(),
                null,
                job.companyType(),
                job.workLocation()
        );

        JobPosting posting = JobPosting.create(
                job.jobTitle() != null ? job.jobTitle() : "Unknown",
                domainCompany,
                JobSource.JOBKOREA
        );

        List<SkillRequirement> skills = new ArrayList<>();
        if (job.skills() != null) {
            job.skills().forEach(skill -> skills.add(new SkillRequirement(skill, true, 1)));
        }

        LocalDate deadline = null;
        if (job.deadlineDate() != null && !job.deadlineDate().isBlank()) {
            try {
                deadline = LocalDate.parse(job.deadlineDate(), DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            } catch (Exception e) {
                try {
                    deadline = LocalDate.parse(job.deadlineDate().substring(0, 10));
                } catch (Exception ignored) {}
            }
        }

        // JobKorea redirects to job detail page - include applicationUrl
        String applicationUrl = job.jobId() != null
                ? JOBKOREA_JOB_URL_PREFIX + job.jobId()
                : job.jobUrl();

        Integer expYears = null;
        if (job.experienceLevel() != null) {
            try {
                String lvl = job.experienceLevel().replaceAll("[^0-9]", "");
                if (!lvl.isBlank()) expYears = Integer.parseInt(lvl);
            } catch (Exception ignored) {}
        }

        posting.update(
                job.jobId(),
                job.jobDescription(),
                skills,
                expYears,
                job.educationLevel(),
                job.salaryInfo(),
                job.workLocation(),
                job.jobType(),
                applicationUrl,
                deadline
        );

        return posting;
    }
}
