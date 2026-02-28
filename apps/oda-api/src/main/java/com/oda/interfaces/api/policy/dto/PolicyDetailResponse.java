package com.oda.interfaces.api.policy.dto;

import com.oda.application.policy.dto.PolicyResult;
import com.oda.domain.policy.PolicyCategory;

import java.time.LocalDate;

public record PolicyDetailResponse(
        Long id,
        String title,
        String summary,
        PolicyCategory category,
        String organizationName,
        LocalDate applicationEndDate,
        String applicationUrl,
        boolean active
) {
    public static PolicyDetailResponse from(PolicyResult result) {
        return new PolicyDetailResponse(
                result.id(),
                result.title(),
                result.summary(),
                result.category(),
                result.organizationName(),
                result.applicationEndDate(),
                result.applicationUrl(),
                result.active()
        );
    }
}
