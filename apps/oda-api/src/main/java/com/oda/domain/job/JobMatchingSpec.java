package com.oda.domain.job;

import com.oda.domain.job.GapAnalysis;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.SkillRequirement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JobMatchingSpec {

    /**
     * Calculate match score (0-100) based on skill match, experience, and education.
     * Weights: skill 60%, experience 30%, education 10%
     */
    public static int calculateMatchScore(JobPosting job, List<String> userSkills,
                                          int experienceYears, String education) {
        int skillScore = calculateSkillScore(job, userSkills);
        int experienceScore = calculateExperienceScore(job, experienceYears);
        int educationScore = calculateEducationScore(job, education);

        return (int) Math.round(skillScore * 0.6 + experienceScore * 0.3 + educationScore * 0.1);
    }

    public static GapAnalysis analyzeGap(JobPosting job, List<String> userSkills,
                                          int experienceYears) {
        List<String> requiredSkillNames = job.getRequiredSkills().stream()
                .map(SkillRequirement::skillName)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        List<String> normalizedUserSkills = userSkills.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        List<String> matchedSkills = requiredSkillNames.stream()
                .filter(normalizedUserSkills::contains)
                .collect(Collectors.toList());

        List<String> missingSkills = requiredSkillNames.stream()
                .filter(s -> !normalizedUserSkills.contains(s))
                .collect(Collectors.toList());

        int matchPercentage = requiredSkillNames.isEmpty() ? 100 :
                (int) Math.round((double) matchedSkills.size() / requiredSkillNames.size() * 100);

        List<String> recommendations = generateRecommendations(missingSkills, job, experienceYears);

        return new GapAnalysis(matchedSkills, missingSkills, matchPercentage, recommendations);
    }

    private static int calculateSkillScore(JobPosting job, List<String> userSkills) {
        List<SkillRequirement> required = job.getRequiredSkills();
        if (required == null || required.isEmpty()) return 100;

        List<String> normalizedUserSkills = userSkills.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        long matched = required.stream()
                .filter(sr -> normalizedUserSkills.contains(sr.skillName().toLowerCase()))
                .count();

        return (int) Math.round((double) matched / required.size() * 100);
    }

    private static int calculateExperienceScore(JobPosting job, int experienceYears) {
        Integer required = job.getRequiredExperienceYears();
        if (required == null || required == 0) return 100;

        if (experienceYears >= required) {
            return 100;
        }
        // Partial credit: proportional
        return (int) Math.round((double) experienceYears / required * 100);
    }

    private static int calculateEducationScore(JobPosting job, String education) {
        String required = job.getRequiredEducation();
        if (required == null || required.isBlank()) return 100;
        if (education == null) return 0;
        // Simple match: if education contains required level keyword
        return education.toLowerCase().contains(required.toLowerCase()) ? 100 : 50;
    }

    private static List<String> generateRecommendations(List<String> missingSkills,
                                                          JobPosting job, int experienceYears) {
        List<String> recommendations = new ArrayList<>();

        for (String skill : missingSkills) {
            recommendations.add(skill + " 스킬을 학습하세요.");
        }

        Integer requiredExp = job.getRequiredExperienceYears();
        if (requiredExp != null && experienceYears < requiredExp) {
            int gap = requiredExp - experienceYears;
            recommendations.add(gap + "년의 추가 경력이 필요합니다.");
        }

        if (missingSkills.isEmpty() && (requiredExp == null || experienceYears >= requiredExp)) {
            recommendations.add("이 공고에 지원하기 좋은 조건입니다.");
        }

        return recommendations;
    }
}
