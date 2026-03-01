package com.oda.domain.job;

import java.util.List;

public record GapAnalysis(
        List<String> matchedSkills,
        List<String> missingSkills,
        int matchPercentage,
        List<String> recommendations
) {}
