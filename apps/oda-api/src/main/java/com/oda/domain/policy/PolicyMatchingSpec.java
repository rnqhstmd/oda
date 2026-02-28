package com.oda.domain.policy;

import com.oda.domain.user.EmploymentStatus;

import java.util.List;

public class PolicyMatchingSpec {

    private PolicyMatchingSpec() {}

    public static MatchResult evaluate(Policy policy, int age, Long personalIncome,
                                       Long householdIncome, String region,
                                       EmploymentStatus status) {
        EligibilityCriteria criteria = policy.getEligibility();
        if (criteria == null) {
            return MatchResult.ofEligible();
        }

        // Check age range
        if (criteria.minAge() != null && age < criteria.minAge()) {
            return MatchResult.ofIneligible("나이가 최소 나이 기준 미달입니다. 최소: " + criteria.minAge() + ", 실제: " + age);
        }
        if (criteria.maxAge() != null && age > criteria.maxAge()) {
            return MatchResult.ofIneligible("나이가 최대 나이 기준을 초과합니다. 최대: " + criteria.maxAge() + ", 실제: " + age);
        }

        // Check personal income limit
        if (criteria.maxPersonalIncome() != null && personalIncome != null
                && personalIncome > criteria.maxPersonalIncome()) {
            return MatchResult.ofIneligible("개인 소득이 기준을 초과합니다. 최대: " + criteria.maxPersonalIncome() + ", 실제: " + personalIncome);
        }

        // Check household income limit
        if (criteria.maxHouseholdIncome() != null && householdIncome != null
                && householdIncome > criteria.maxHouseholdIncome()) {
            return MatchResult.ofIneligible("가구 소득이 기준을 초과합니다. 최대: " + criteria.maxHouseholdIncome() + ", 실제: " + householdIncome);
        }

        // Check region
        List<String> requiredRegions = criteria.requiredRegions();
        if (requiredRegions != null && !requiredRegions.isEmpty() && region != null) {
            boolean regionMatches = requiredRegions.stream()
                    .anyMatch(r -> region.contains(r) || r.contains(region));
            if (!regionMatches) {
                return MatchResult.ofIneligible("거주 지역이 대상 지역에 포함되지 않습니다. 대상 지역: " + requiredRegions + ", 실제: " + region);
            }
        }

        // Check employment status
        List<EmploymentStatus> targetStatuses = criteria.targetEmploymentStatuses();
        if (targetStatuses != null && !targetStatuses.isEmpty() && status != null) {
            if (!targetStatuses.contains(status)) {
                return MatchResult.ofIneligible("취업 상태가 대상에 포함되지 않습니다. 대상: " + targetStatuses + ", 실제: " + status);
            }
        }

        return MatchResult.ofEligible();
    }
}
