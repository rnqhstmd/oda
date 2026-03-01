package com.oda.infrastructure.policy.persistence;

import com.oda.domain.policy.EligibilityCriteria;
import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyCategory;
import com.oda.domain.user.EmploymentStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PolicyMapper {

    public Policy toDomain(PolicyJpaEntity entity) {
        EligibilityCriteria eligibility = new EligibilityCriteria(
                entity.getMinAge(),
                entity.getMaxAge(),
                entity.getMaxPersonalIncome(),
                entity.getMaxHouseholdIncome(),
                entity.getMaxMedianIncomePercent(),
                parseStringList(entity.getRequiredRegions()),
                parseEmploymentStatuses(entity.getTargetEmploymentStatuses()),
                parseStringList(entity.getExcludeConditions())
        );

        return Policy.reconstruct(
                entity.getId(),
                entity.getExternalId(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getOrganizationName(),
                eligibility,
                entity.getApplicationStartDate(),
                entity.getApplicationEndDate(),
                entity.getApplicationUrl(),
                entity.getLastSyncedAt(),
                entity.isActive()
        );
    }

    public PolicyJpaEntity toEntity(Policy policy) {
        EligibilityCriteria eligibility = policy.getEligibility();
        String requiredRegions = toCommaString(eligibility != null ? eligibility.requiredRegions() : null);
        String targetStatuses = toEmploymentStatusString(eligibility != null ? eligibility.targetEmploymentStatuses() : null);
        String excludeConditions = toCommaString(eligibility != null ? eligibility.excludeConditions() : null);

        return PolicyJpaEntity.create(
                policy.getExternalId(),
                policy.getTitle(),
                policy.getSummary(),
                policy.getDescription(),
                policy.getCategory(),
                policy.getOrganizationName(),
                eligibility != null ? eligibility.minAge() : null,
                eligibility != null ? eligibility.maxAge() : null,
                eligibility != null ? eligibility.maxPersonalIncome() : null,
                eligibility != null ? eligibility.maxHouseholdIncome() : null,
                eligibility != null ? eligibility.maxMedianIncomePercent() : null,
                requiredRegions,
                targetStatuses,
                excludeConditions,
                policy.getApplicationStartDate(),
                policy.getApplicationEndDate(),
                policy.getApplicationUrl(),
                policy.getLastSyncedAt(),
                policy.isActive()
        );
    }

    private List<String> parseStringList(String csv) {
        if (csv == null || csv.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private List<EmploymentStatus> parseEmploymentStatuses(String csv) {
        if (csv == null || csv.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(EmploymentStatus::valueOf)
                .collect(Collectors.toList());
    }

    private String toCommaString(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        return String.join(",", list);
    }

    private String toEmploymentStatusString(List<EmploymentStatus> list) {
        if (list == null || list.isEmpty()) return null;
        return list.stream().map(Enum::name).collect(Collectors.joining(","));
    }
}
