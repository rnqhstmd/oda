package com.oda.infrastructure.user.persistence;

import com.oda.domain.user.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;
        User user = User.createFromOAuth(entity.getOauthProvider(), entity.getOauthId(),
                entity.getEmail(), entity.getName());
        setUserId(user, entity.getId());
        user.updateConsent(entity.isConsentPersonalInfo(), entity.isConsentSensitiveInfo());
        return user;
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
        UserProfile profile = UserProfile.create(entity.getUserId());
        setProfileId(profile, entity.getId());

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

        List<String> skills = parseCommaSeparated(entity.getSkills());
        List<String> targetJobCategories = parseCommaSeparated(entity.getTargetJobCategories());

        profile.update(entity.getBirthDate(), entity.getSido(), entity.getSigungu(),
                entity.getEmploymentStatus(), educations, workExperiences, certifications,
                skills, targetJobCategories);
        if (incomeInfo != null) {
            profile.updateIncomeInfo(incomeInfo);
        }
        return profile;
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

        String skills = profile.getSkills() == null ? null
                : String.join(",", profile.getSkills());
        String targetJobCategories = profile.getTargetJobCategories() == null ? null
                : String.join(",", profile.getTargetJobCategories());

        entity.update(profile.getBirthDate(), profile.getSido(), profile.getSigungu(),
                personalIncome, householdIncome, householdSize,
                profile.getEmploymentStatus(), skills, targetJobCategories,
                educations, certifications, workExperiences);
        return entity;
    }

    private List<String> parseCommaSeparated(String value) {
        if (value == null || value.isBlank()) return Collections.emptyList();
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    // Reflection-free id injection via package-private setters on domain objects.
    // Domain classes use private constructors, so we set id via reflection here.
    private void setUserId(User user, Long id) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set user id", e);
        }
    }

    private void setProfileId(UserProfile profile, Long id) {
        try {
            var field = UserProfile.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(profile, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set profile id", e);
        }
    }
}
