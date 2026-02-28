package com.oda.infrastructure.job.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SaraminJobResponse(
        @JsonProperty("jobs") Jobs jobs
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Jobs(
            @JsonProperty("job") List<Job> job
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Job(
            @JsonProperty("id") String id,
            @JsonProperty("url") String url,
            @JsonProperty("active") int active,
            @JsonProperty("position") Position position,
            @JsonProperty("company") Company company,
            @JsonProperty("salary") Salary salary
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Position(
            @JsonProperty("title") String title,
            @JsonProperty("location") Location location,
            @JsonProperty("job-type") JobType jobType,
            @JsonProperty("experience-level") ExperienceLevel experienceLevel,
            @JsonProperty("required-education-level") RequiredEducationLevel requiredEducationLevel,
            @JsonProperty("job-mid-code") JobMidCode jobMidCode,
            @JsonProperty("expiration-date") String expirationDate,
            @JsonProperty("description") String description
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location(@JsonProperty("name") String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JobType(@JsonProperty("name") String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ExperienceLevel(
            @JsonProperty("min") int min,
            @JsonProperty("name") String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RequiredEducationLevel(@JsonProperty("name") String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JobMidCode(@JsonProperty("name") String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Company(
            @JsonProperty("detail") CompanyDetail detail
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CompanyDetail(
            @JsonProperty("name") String name,
            @JsonProperty("industry") String industry,
            @JsonProperty("size") String size,
            @JsonProperty("location") String location
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Salary(@JsonProperty("name") String name) {}
}
