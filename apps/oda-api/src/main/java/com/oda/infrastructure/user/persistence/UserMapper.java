package com.oda.infrastructure.user.persistence;

import com.oda.domain.user.*;
import com.oda.infrastructure.persistence.CsvStringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;
        return User.reconstruct(entity.getId(), entity.getEmail(), entity.getName(),
                entity.getOauthProvider(), entity.getOauthId(), entity.getPasswordHash(),
                entity.isConsentPersonalInfo(), entity.isConsentSensitiveInfo());
    }

    public UserJpaEntity toEntity(User user) {
        if (user == null) return null;
        if (user.getId() == null) {
            return UserJpaEntity.create(user.getEmail(), user.getName(),
                    user.getOauthProvider(), user.getOauthId(), user.getPasswordHash(),
                    user.isConsentPersonalInfo(), user.isConsentSensitiveInfo());
        }
        // For updates, we need to find the existing entity - handled in repository
        return UserJpaEntity.create(user.getEmail(), user.getName(),
                user.getOauthProvider(), user.getOauthId(), user.getPasswordHash(),
                user.isConsentPersonalInfo(), user.isConsentSensitiveInfo());
    }

    public UserProfile toDomain(UserProfileJpaEntity entity) {
        if (entity == null) return null;

        IncomeInfo incomeInfo = null;
        if (entity.getPersonalIncome() != null || entity.getHouseholdIncome() != null
                || entity.getHouseholdSize() != null) {
            incomeInfo = new IncomeInfo(entity.getPersonalIncome(), entity.getHouseholdIncome(),
                    entity.getHouseholdSize());
        }

        List<Education> educations = entity.getEducations().stream()
                .map(e -> new Education(e.getSchoolName(), e.getMajor(), e.getDegree(),
                        e.getGraduated() != null && e.getGraduated()))
                .collect(Collectors.toList());

        List<WorkExperience> workExperiences = entity.getWorkExperiences().stream()
                .map(w -> new WorkExperience(w.getCompanyName(), w.getPosition(),
                        w.getStartDate(), w.getEndDate(), w.isCurrent()))
                .collect(Collectors.toList());

        List<Certification> certifications = entity.getCertifications().stream()
                .map(c -> new Certification(c.getName(), c.getIssuer(), c.getAcquiredDate()))
                .collect(Collectors.toList());

        List<String> skills = CsvStringUtils.parse(entity.getSkills());
        List<String> targetJobCategories = CsvStringUtils.parse(entity.getTargetJobCategories());

        return UserProfile.reconstruct(entity.getId(), entity.getUserId(),
                entity.getBirthDate(), entity.getSido(), entity.getSigungu(),
                incomeInfo, entity.getEmploymentStatus(),
                educations, workExperiences, certifications, skills, targetJobCategories);
    }

    public UserProfileJpaEntity toEntity(UserProfile profile) {
        if (profile == null) return null;
        UserProfileJpaEntity entity = UserProfileJpaEntity.create(profile.getUserId());

        Long personalIncome = null;
        Long householdIncome = null;
        Integer householdSize = null;
        if (profile.getIncomeInfo() != null) {
            personalIncome = profile.getIncomeInfo().personalIncome();
            householdIncome = profile.getIncomeInfo().householdIncome();
            householdSize = profile.getIncomeInfo().householdSize();
        }

        List<EducationJpaEntity> educations = profile.getEducations() == null
                ? Collections.emptyList()
                : profile.getEducations().stream()
                        .map(e -> EducationJpaEntity.create(e.schoolName(), e.major(),
                                e.degree(), e.graduated()))
                        .collect(Collectors.toList());

        List<WorkExperienceJpaEntity> workExperiences = profile.getWorkExperiences() == null
                ? Collections.emptyList()
                : profile.getWorkExperiences().stream()
                        .map(w -> WorkExperienceJpaEntity.create(w.companyName(), w.position(),
                                w.startDate(), w.endDate(), w.isCurrent()))
                        .collect(Collectors.toList());

        List<CertificationJpaEntity> certifications = profile.getCertifications() == null
                ? Collections.emptyList()
                : profile.getCertifications().stream()
                        .map(c -> CertificationJpaEntity.create(c.name(), c.issuer(), c.acquiredDate()))
                        .collect(Collectors.toList());

        String skills = CsvStringUtils.join(profile.getSkills());
        String targetJobCategories = CsvStringUtils.join(profile.getTargetJobCategories());

        entity.update(profile.getBirthDate(), profile.getSido(), profile.getSigungu(),
                personalIncome, householdIncome, householdSize,
                profile.getEmploymentStatus(), skills, targetJobCategories,
                educations, certifications, workExperiences);
        return entity;
    }


}
