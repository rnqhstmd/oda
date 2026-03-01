package com.oda.interfaces.api.user.dto;

import com.oda.application.user.dto.ProfileResult;
import com.oda.domain.user.Certification;
import com.oda.domain.user.Education;
import com.oda.domain.user.EmploymentStatus;
import com.oda.domain.user.IncomeInfo;
import com.oda.domain.user.WorkExperience;

import java.time.LocalDate;
import java.util.List;

public record ProfileResponse(
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
    public static ProfileResponse from(ProfileResult result) {
        return new ProfileResponse(
                result.id(),
                result.userId(),
                result.birthDate(),
                result.age(),
                result.sido(),
                result.sigungu(),
                result.incomeInfo(),
                result.employmentStatus(),
                result.educations(),
                result.workExperiences(),
                result.certifications(),
                result.skills(),
                result.targetJobCategories()
        );
    }
}
