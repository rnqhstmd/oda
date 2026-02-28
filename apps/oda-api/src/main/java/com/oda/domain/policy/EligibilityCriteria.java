package com.oda.domain.policy;

import com.oda.domain.user.EmploymentStatus;

import java.util.List;

public record EligibilityCriteria(
        Integer minAge,
        Integer maxAge,
        Long maxPersonalIncome,
        Long maxHouseholdIncome,
        Integer maxMedianIncomePercent,
        List<String> requiredRegions,
        List<EmploymentStatus> targetEmploymentStatuses,
        List<String> excludeConditions
) {}
