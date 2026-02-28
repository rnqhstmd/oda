package com.oda.interfaces.api.job.dto;

import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;
import com.oda.domain.job.SkillRequirement;

import java.time.LocalDate;
import java.util.List;

public record JobDetailResponse(Long id, String title, String companyName, String companyIndustry,
                                String description, List<SkillRequirement> requiredSkills,
                                Integer requiredExperienceYears, String requiredEducation,
                                String salary, String location, String jobType,
                                JobSource source, String applicationUrl,
                                LocalDate applicationDeadline, boolean applicationOpen) {

    public static JobDetailResponse from(JobPosting job) {
        return new JobDetailResponse(
                job.getId(),
                job.getTitle(),
                job.getCompany() != null ? job.getCompany().name() : null,
                job.getCompany() != null ? job.getCompany().industry() : null,
                job.getDescription(),
                job.getRequiredSkills(),
                job.getRequiredExperienceYears(),
                job.getRequiredEducation(),
                job.getSalary(),
                job.getLocation(),
                job.getJobType(),
                job.getSource(),
                job.getApplicationUrl(),
                job.getApplicationDeadline(),
                job.isApplicationOpen()
        );
    }
}
