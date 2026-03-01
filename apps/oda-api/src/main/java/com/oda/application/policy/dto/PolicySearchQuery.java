package com.oda.application.policy.dto;

import com.oda.domain.policy.PolicyCategory;
import org.springframework.data.domain.Pageable;

public record PolicySearchQuery(
        PolicyCategory category,
        String keyword,
        Pageable pageable
) {}
