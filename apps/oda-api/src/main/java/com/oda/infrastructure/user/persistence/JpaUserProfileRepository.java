package com.oda.infrastructure.user.persistence;

import com.oda.domain.user.UserProfile;
import com.oda.domain.user.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

interface SpringDataUserProfileRepository extends JpaRepository<UserProfileJpaEntity, Long> {
    Optional<UserProfileJpaEntity> findByUserId(Long userId);
}

@Repository
@RequiredArgsConstructor
public class JpaUserProfileRepository implements UserProfileRepository {

    private final SpringDataUserProfileRepository springRepo;
    private final UserMapper mapper;

    @Override
    public UserProfile save(UserProfile profile) {
        Optional<UserProfileJpaEntity> existing = springRepo.findByUserId(profile.getUserId());
        UserProfileJpaEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            com.oda.domain.user.IncomeInfo inc = profile.getIncomeInfo();
            Long personalIncome = inc != null ? inc.personalIncome() : null;
            Long householdIncome = inc != null ? inc.householdIncome() : null;
            Integer householdSize = inc != null ? inc.householdSize() : null;

            java.util.List<EducationJpaEntity> educations = profile.getEducations() == null
                    ? java.util.Collections.emptyList()
                    : profile.getEducations().stream()
                            .map(e -> EducationJpaEntity.create(e.schoolName(), e.major(), e.degree(), e.graduated()))
                            .collect(java.util.stream.Collectors.toList());

            java.util.List<WorkExperienceJpaEntity> workExperiences = profile.getWorkExperiences() == null
                    ? java.util.Collections.emptyList()
                    : profile.getWorkExperiences().stream()
                            .map(w -> WorkExperienceJpaEntity.create(w.companyName(), w.position(),
                                    w.startDate(), w.endDate(), w.isCurrent()))
                            .collect(java.util.stream.Collectors.toList());

            java.util.List<CertificationJpaEntity> certifications = profile.getCertifications() == null
                    ? java.util.Collections.emptyList()
                    : profile.getCertifications().stream()
                            .map(c -> CertificationJpaEntity.create(c.name(), c.issuer(), c.acquiredDate()))
                            .collect(java.util.stream.Collectors.toList());

            String skills = profile.getSkills() == null ? null : String.join(",", profile.getSkills());
            String targetJobCategories = profile.getTargetJobCategories() == null ? null
                    : String.join(",", profile.getTargetJobCategories());

            entity.update(profile.getBirthDate(), profile.getSido(), profile.getSigungu(),
                    personalIncome, householdIncome, householdSize, profile.getEmploymentStatus(),
                    skills, targetJobCategories, educations, certifications, workExperiences);
        } else {
            entity = mapper.toEntity(profile);
        }
        return mapper.toDomain(springRepo.save(entity));
    }

    @Override
    public Optional<UserProfile> findByUserId(Long userId) {
        return springRepo.findByUserId(userId).map(mapper::toDomain);
    }
}
