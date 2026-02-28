package com.oda.interfaces.api.policy.dto;

import com.oda.application.policy.dto.PolicyResult;
import com.oda.domain.policy.PolicyCategory;

import java.time.LocalDate;

public record PolicyResponse(
        Long id,
        String title,
        String summary,
        PolicyCategory category,
        String organizationName,
        LocalDate applicationEndDate,
        String applicationUrl,
        boolean active
) {
    public static PolicyResponse from(PolicyResult result) {
        return new PolicyResponse(
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
