package com.oda.domain.job;

import com.oda.domain.common.BaseEntity;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class JobPosting extends BaseEntity {
    private Long id;
    private String externalId;
    private String title;
    private Company company;
    private String description;
    private List<SkillRequirement> requiredSkills;
    private Integer requiredExperienceYears;
    private String requiredEducation;
    private String salary;
    private String location;
    private String jobType;
    private JobSource source;
    private String applicationUrl;
    private LocalDate applicationDeadline;
    private LocalDateTime lastSyncedAt;
    private boolean active;

    private JobPosting() {}

    public static JobPosting create(String title, Company company, JobSource source) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (company == null) {
            throw new IllegalArgumentException("company must not be null");
        }
        if (source == null) {
            throw new IllegalArgumentException("source must not be null");
        }
        JobPosting job = new JobPosting();
        job.title = title;
        job.company = company;
        job.source = source;
        job.requiredSkills = new ArrayList<>();
        job.active = true;
        job.lastSyncedAt = LocalDateTime.now();
        return job;
    }

    public void update(String externalId, String description, List<SkillRequirement> requiredSkills,
                       Integer requiredExperienceYears, String requiredEducation,
                       String salary, String location, String jobType,
                       String applicationUrl, LocalDate applicationDeadline) {
        this.externalId = externalId;
        this.description = description;
        if (requiredSkills != null) this.requiredSkills = new ArrayList<>(requiredSkills);
        this.requiredExperienceYears = requiredExperienceYears;
        this.requiredEducation = requiredEducation;
        this.salary = salary;
        this.location = location;
        this.jobType = jobType;
        this.applicationUrl = applicationUrl;
        this.applicationDeadline = applicationDeadline;
        this.lastSyncedAt = LocalDateTime.now();
    }

    public boolean isApplicationOpen() {
        if (!active) return false;
        if (applicationDeadline == null) return true;
        return !applicationDeadline.isBefore(LocalDate.now());
    }

    public void deactivate() {
        this.active = false;
    }

    public static JobPosting reconstruct(Long id, String title, Company company, JobSource source,
                                          String externalId, String description,
                                          List<SkillRequirement> requiredSkills,
                                          Integer requiredExperienceYears, String requiredEducation,
                                          String salary, String location, String jobType,
                                          String applicationUrl, LocalDate applicationDeadline,
                                          LocalDateTime lastSyncedAt, boolean active) {
        JobPosting job = new JobPosting();
        job.id = id;
        job.title = title;
        job.company = company;
        job.source = source;
        job.externalId = externalId;
        job.description = description;
        job.requiredSkills = requiredSkills != null ? new ArrayList<>(requiredSkills) : new ArrayList<>();
        job.requiredExperienceYears = requiredExperienceYears;
        job.requiredEducation = requiredEducation;
        job.salary = salary;
        job.location = location;
        job.jobType = jobType;
        job.applicationUrl = applicationUrl;
        job.applicationDeadline = applicationDeadline;
        job.lastSyncedAt = lastSyncedAt;
        job.active = active;
        return job;
    }
}
