package com.oda.infrastructure.policy.persistence;

import com.oda.domain.common.BaseEntity;
import com.oda.domain.policy.PolicyCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "policies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PolicyJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String externalId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private PolicyCategory category;

    private String organizationName;

    private Integer minAge;
    private Integer maxAge;
    private Long maxPersonalIncome;
    private Long maxHouseholdIncome;
    private Integer maxMedianIncomePercent;

    @Column(columnDefinition = "TEXT")
    private String requiredRegions;

    @Column(columnDefinition = "TEXT")
    private String targetEmploymentStatuses;

    @Column(columnDefinition = "TEXT")
    private String excludeConditions;

    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private String applicationUrl;
    private LocalDateTime lastSyncedAt;

    @Column(name = "is_active")
    private boolean active;

    public static PolicyJpaEntity create(
            String externalId, String title, String summary, String description,
            PolicyCategory category, String organizationName,
            Integer minAge, Integer maxAge,
            Long maxPersonalIncome, Long maxHouseholdIncome, Integer maxMedianIncomePercent,
            String requiredRegions, String targetEmploymentStatuses, String excludeConditions,
            LocalDate applicationStartDate, LocalDate applicationEndDate,
            String applicationUrl, LocalDateTime lastSyncedAt, boolean active) {
        PolicyJpaEntity entity = new PolicyJpaEntity();
        entity.externalId = externalId;
        entity.title = title;
        entity.summary = summary;
        entity.description = description;
        entity.category = category;
        entity.organizationName = organizationName;
        entity.minAge = minAge;
        entity.maxAge = maxAge;
        entity.maxPersonalIncome = maxPersonalIncome;
        entity.maxHouseholdIncome = maxHouseholdIncome;
        entity.maxMedianIncomePercent = maxMedianIncomePercent;
        entity.requiredRegions = requiredRegions;
        entity.targetEmploymentStatuses = targetEmploymentStatuses;
        entity.excludeConditions = excludeConditions;
        entity.applicationStartDate = applicationStartDate;
        entity.applicationEndDate = applicationEndDate;
        entity.applicationUrl = applicationUrl;
        entity.lastSyncedAt = lastSyncedAt;
        entity.active = active;
        return entity;
    }

    public void update(
            String title, String summary, String description,
            String organizationName,
            Integer minAge, Integer maxAge,
            Long maxPersonalIncome, Long maxHouseholdIncome, Integer maxMedianIncomePercent,
            String requiredRegions, String targetEmploymentStatuses, String excludeConditions,
            LocalDate applicationStartDate, LocalDate applicationEndDate,
            String applicationUrl, LocalDateTime lastSyncedAt, boolean active) {
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.organizationName = organizationName;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.maxPersonalIncome = maxPersonalIncome;
        this.maxHouseholdIncome = maxHouseholdIncome;
        this.maxMedianIncomePercent = maxMedianIncomePercent;
        this.requiredRegions = requiredRegions;
        this.targetEmploymentStatuses = targetEmploymentStatuses;
        this.excludeConditions = excludeConditions;
        this.applicationStartDate = applicationStartDate;
        this.applicationEndDate = applicationEndDate;
        this.applicationUrl = applicationUrl;
        this.lastSyncedAt = lastSyncedAt;
        this.active = active;
    }
}
