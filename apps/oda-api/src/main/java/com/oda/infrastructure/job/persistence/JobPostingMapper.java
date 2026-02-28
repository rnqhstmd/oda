package com.oda.infrastructure.job.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oda.domain.job.Company;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.SkillRequirement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JobPostingMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JobPosting toDomain(JobPostingJpaEntity entity) {
        // DB schema only stores company_name and location; reconstruct Company with those fields
        Company company = new Company(entity.getCompanyName(), null, null, entity.getLocation());

        return JobPosting.reconstruct(
                entity.getId(),
                entity.getTitle(),
                company,
                entity.getSource(),
                entity.getExternalId(),
                entity.getDescription(),
                parseSkills(entity.getRequiredSkills()),
                entity.getRequiredExperienceYears(),
                entity.getRequiredEducation(),
                entity.getSalary(),
                entity.getLocation(),
                entity.getJobType(),
                entity.getApplicationUrl(),
                entity.getApplicationDeadline(),
                entity.getLastSyncedAt(),
                entity.isActive()
        );
    }

    public JobPostingJpaEntity toEntity(JobPosting domain) {
        Company company = domain.getCompany();
        // Use job location field; company location stored separately in domain but DB only has location column
        String location = domain.getLocation() != null ? domain.getLocation()
                : (company != null ? company.location() : null);
        return JobPostingJpaEntity.create(
                domain.getExternalId(),
                domain.getTitle(),
                company != null ? company.name() : null,
                location,
                domain.getDescription(),
                serializeSkills(domain.getRequiredSkills()),
                domain.getRequiredExperienceYears(),
                domain.getRequiredEducation(),
                domain.getSalary(),
                domain.getJobType(),
                domain.getSource(),
                domain.getApplicationUrl(),
                domain.getApplicationDeadline(),
                domain.getLastSyncedAt(),
                domain.isActive()
        );
    }

    private List<SkillRequirement> parseSkills(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<SkillRequirement>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    private String serializeSkills(List<SkillRequirement> skills) {
        if (skills == null || skills.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(skills);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
