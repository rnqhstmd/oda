package com.oda.infrastructure.job.persistence;

import com.oda.domain.common.BaseEntity;
import com.oda.domain.job.JobSource;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_postings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPostingJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true)
    private String externalId;

    @Column(nullable = false)
    private String title;

    @Column(name = "company_name")
    private String companyName;

    // industry, companySize, companyLocation are stored as part of description/skills JSON
    // since the DB schema only has company_name and location columns
    @Column(name = "location")
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "skill_requirements", columnDefinition = "TEXT")
    private String requiredSkills;

    @Column(name = "min_experience_years")
    private Integer requiredExperienceYears;

    @Column(name = "education_level")
    private String requiredEducation;

    @Column(name = "salary_info")
    private String salary;

    @Column(name = "job_type")
    private String jobType;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_source")
    private JobSource source;

    @Column(name = "job_url")
    private String applicationUrl;

    @Column(name = "deadline")
    private LocalDate applicationDeadline;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Column(name = "is_active")
    private boolean active;

    public static JobPostingJpaEntity create(String externalId, String title,
                                              String companyName, String location,
                                              String description, String requiredSkills,
                                              Integer requiredExperienceYears, String requiredEducation,
                                              String salary, String jobType,
                                              JobSource source, String applicationUrl,
                                              LocalDate applicationDeadline, LocalDateTime lastSyncedAt,
                                              boolean active) {
        JobPostingJpaEntity entity = new JobPostingJpaEntity();
        entity.externalId = externalId;
        entity.title = title;
        entity.companyName = companyName;
        entity.location = location;
        entity.description = description;
        entity.requiredSkills = requiredSkills;
        entity.requiredExperienceYears = requiredExperienceYears;
        entity.requiredEducation = requiredEducation;
        entity.salary = salary;
        entity.jobType = jobType;
        entity.source = source;
        entity.applicationUrl = applicationUrl;
        entity.applicationDeadline = applicationDeadline;
        entity.lastSyncedAt = lastSyncedAt;
        entity.active = active;
        return entity;
    }
}
