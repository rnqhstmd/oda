package com.oda.domain.simulation;

import java.util.List;

public record SimulationResult(
        String currentPosition,
        String targetPosition,
        CareerPath careerPath,
        List<String> gapItems
) {}
