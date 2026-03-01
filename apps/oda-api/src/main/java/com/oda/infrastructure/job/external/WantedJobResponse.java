package com.oda.infrastructure.job.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WantedJobResponse(
        @JsonProperty("data") List<Job> data,
        @JsonProperty("links") Links links
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Job(
            @JsonProperty("id") Long id,
            @JsonProperty("title") String title,
            @JsonProperty("url") String url,
            @JsonProperty("company") Company company,
            @JsonProperty("address") Address address,
            @JsonProperty("due_time") String dueTime,
            @JsonProperty("experience_min") int experienceMin,
            @JsonProperty("experience_max") int experienceMax,
            @JsonProperty("skills") List<String> skills,
            @JsonProperty("job_type") String jobType,
            @JsonProperty("detail") Detail detail
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Company(
            @JsonProperty("name") String name,
            @JsonProperty("industry_name") String industryName,
            @JsonProperty("type") String type
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Address(
            @JsonProperty("country") String country,
            @JsonProperty("location") String location
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Detail(
            @JsonProperty("intro") String intro,
            @JsonProperty("main_tasks") String mainTasks,
            @JsonProperty("requirements") String requirements,
            @JsonProperty("preferred_points") String preferredPoints
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Links(
            @JsonProperty("next") String next
    ) {}
}
