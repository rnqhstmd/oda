package com.oda.infrastructure.policy.persistence;

import com.oda.domain.policy.EligibilityCriteria;
import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyCategory;
import com.oda.domain.user.EmploymentStatus;
import com.oda.infrastructure.persistence.CsvStringUtils;
import org.springframework.stereotype.Component;

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
                CsvStringUtils.parse(entity.getRequiredRegions()),
                parseEmploymentStatuses(entity.getTargetEmploymentStatuses()),
                CsvStringUtils.parse(entity.getExcludeConditions())
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
        String requiredRegions = CsvStringUtils.join(eligibility != null ? eligibility.requiredRegions() : null);
        String targetStatuses = toEmploymentStatusString(eligibility != null ? eligibility.targetEmploymentStatuses() : null);
        String excludeConditions = CsvStringUtils.join(eligibility != null ? eligibility.excludeConditions() : null);

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

    private List<EmploymentStatus> parseEmploymentStatuses(String csv) {
        return CsvStringUtils.parse(csv).stream()
                .map(EmploymentStatus::valueOf)
                .collect(Collectors.toList());
    }

    private String toEmploymentStatusString(List<EmploymentStatus> list) {
        if (list == null || list.isEmpty()) return null;
        return list.stream().map(Enum::name).collect(Collectors.joining(","));
    }
}
