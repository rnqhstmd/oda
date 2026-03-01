package com.oda.domain.simulation;

import java.util.List;

public record CareerPath(
        List<CareerStep> steps,
        int totalEstimatedMonths,
        List<String> requiredCertifications
) {}
