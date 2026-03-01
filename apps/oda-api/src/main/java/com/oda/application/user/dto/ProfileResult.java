package com.oda.application.user.dto;

import com.oda.domain.user.Certification;
import com.oda.domain.user.Education;
import com.oda.domain.user.EmploymentStatus;
import com.oda.domain.user.IncomeInfo;
import com.oda.domain.user.UserProfile;
import com.oda.domain.user.WorkExperience;

import java.time.LocalDate;
import java.util.List;

public record ProfileResult(
        Long id,
        Long userId,
        LocalDate birthDate,
        Integer age,
        String sido,
        String sigungu,
        IncomeInfo incomeInfo,
        EmploymentStatus employmentStatus,
        List<Education> educations,
        List<WorkExperience> workExperiences,
        List<Certification> certifications,
        List<String> skills,
        List<String> targetJobCategories
) {
    public static ProfileResult from(UserProfile profile) {
        Integer age = null;
        if (profile.getBirthDate() != null) {
            try {
                age = profile.calculateAge();
            } catch (IllegalStateException ignored) {}
        }
        return new ProfileResult(
                profile.getId(),
                profile.getUserId(),
                profile.getBirthDate(),
                age,
                profile.getSido(),
                profile.getSigungu(),
                profile.getIncomeInfo(),
                profile.getEmploymentStatus(),
                profile.getEducations(),
                profile.getWorkExperiences(),
                profile.getCertifications(),
                profile.getSkills(),
                profile.getTargetJobCategories()
        );
    }
}
