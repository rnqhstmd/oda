package com.oda.infrastructure.job.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JobKoreaJobResponse(
        @JsonProperty("result") String result,
        @JsonProperty("totalCount") int totalCount,
        @JsonProperty("jobs") List<Job> jobs
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Job(
            @JsonProperty("jobId") String jobId,
            @JsonProperty("jobTitle") String jobTitle,
            @JsonProperty("companyName") String companyName,
            @JsonProperty("companyType") String companyType,
            @JsonProperty("workLocation") String workLocation,
            @JsonProperty("salaryInfo") String salaryInfo,
            @JsonProperty("experienceLevel") String experienceLevel,
            @JsonProperty("educationLevel") String educationLevel,
            @JsonProperty("jobType") String jobType,
            @JsonProperty("skills") List<String> skills,
            @JsonProperty("deadlineDate") String deadlineDate,
            @JsonProperty("jobUrl") String jobUrl,
            @JsonProperty("jobDescription") String jobDescription
    ) {}
}
