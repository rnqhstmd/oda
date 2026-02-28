package com.oda.domain.simulation;

import java.util.List;

public record CareerStep(
        String title,
        String description,
        int estimatedMonths,
        List<String> requiredSkills
) {}
