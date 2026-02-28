package com.oda.domain.policy;

import com.oda.domain.common.BaseEntity;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class Policy extends BaseEntity {

    private Long id;
    private String externalId;
    private String title;
    private String summary;
    private String description;
    private PolicyCategory category;
    private String organizationName;
    private EligibilityCriteria eligibility;
    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private String applicationUrl;
    private LocalDateTime lastSyncedAt;
    private boolean active;

    private Policy() {}

    public static Policy reconstruct(Long id, String externalId, String title, String summary,
                                     String description, PolicyCategory category,
                                     String organizationName, EligibilityCriteria eligibility,
                                     LocalDate applicationStartDate, LocalDate applicationEndDate,
                                     String applicationUrl, LocalDateTime lastSyncedAt, boolean active) {
        Policy policy = new Policy();
        policy.id = id;
        policy.externalId = externalId;
        policy.title = title;
        policy.summary = summary;
        policy.description = description;
        policy.category = category;
        policy.organizationName = organizationName;
        policy.eligibility = eligibility;
        policy.applicationStartDate = applicationStartDate;
        policy.applicationEndDate = applicationEndDate;
        policy.applicationUrl = applicationUrl;
        policy.lastSyncedAt = lastSyncedAt;
        policy.active = active;
        return policy;
    }

    public static Policy create(String title, PolicyCategory category, EligibilityCriteria eligibility) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (category == null) {
            throw new IllegalArgumentException("category must not be null");
        }
        Policy policy = new Policy();
        policy.title = title;
        policy.category = category;
        policy.eligibility = eligibility;
        policy.active = true;
        return policy;
    }

    public void update(String title, String summary, String description,
                       String organizationName, EligibilityCriteria eligibility,
                       LocalDate applicationStartDate, LocalDate applicationEndDate,
                       String applicationUrl) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        this.summary = summary;
        this.description = description;
        this.organizationName = organizationName;
        this.eligibility = eligibility;
        this.applicationStartDate = applicationStartDate;
        this.applicationEndDate = applicationEndDate;
        this.applicationUrl = applicationUrl;
    }

    public void markSynced(LocalDateTime syncedAt) {
        this.lastSyncedAt = syncedAt;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public boolean isApplicationOpen() {
        if (applicationEndDate == null) {
            return false;
        }
        return !applicationEndDate.isBefore(LocalDate.now());
    }
}
