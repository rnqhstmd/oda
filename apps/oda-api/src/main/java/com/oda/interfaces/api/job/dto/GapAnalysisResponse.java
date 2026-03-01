package com.oda.interfaces.api.job.dto;

import com.oda.domain.job.GapAnalysis;

import java.util.List;

public record GapAnalysisResponse(List<String> matchedSkills, List<String> missingSkills,
                                  int matchPercentage, List<String> recommendations) {

    public static GapAnalysisResponse from(GapAnalysis gap) {
        return new GapAnalysisResponse(
                gap.matchedSkills(),
                gap.missingSkills(),
                gap.matchPercentage(),
                gap.recommendations()
        );
    }
}
