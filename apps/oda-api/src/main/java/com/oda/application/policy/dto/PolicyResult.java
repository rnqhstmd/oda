package com.oda.application.policy.dto;

import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyCategory;

import java.time.LocalDate;

public record PolicyResult(
        Long id,
        String title,
        String summary,
        PolicyCategory category,
        String organizationName,
        LocalDate applicationEndDate,
        String applicationUrl,
        boolean active
) {
    public static PolicyResult from(Policy policy) {
        return new PolicyResult(
                policy.getId(),
                policy.getTitle(),
                policy.getSummary(),
                policy.getCategory(),
                policy.getOrganizationName(),
                policy.getApplicationEndDate(),
                policy.getApplicationUrl(),
                policy.isActive()
        );
    }
}
