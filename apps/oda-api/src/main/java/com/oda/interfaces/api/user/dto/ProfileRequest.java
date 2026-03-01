package com.oda.interfaces.api.user.dto;

import com.oda.domain.user.Certification;
import com.oda.domain.user.Education;
import com.oda.domain.user.EmploymentStatus;
import com.oda.domain.user.WorkExperience;

import java.time.LocalDate;
import java.util.List;

public record ProfileRequest(
        LocalDate birthDate,
        String sido,
        String sigungu,
        Long personalIncome,
        Long householdIncome,
        Integer householdSize,
        EmploymentStatus employmentStatus,
        List<Education> educations,
        List<WorkExperience> workExperiences,
        List<Certification> certifications,
        List<String> skills,
        List<String> targetJobCategories
) {}
