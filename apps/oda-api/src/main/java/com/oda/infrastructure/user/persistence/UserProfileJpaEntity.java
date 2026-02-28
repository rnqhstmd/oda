package com.oda.infrastructure.user.persistence;

import com.oda.domain.common.BaseEntity;
import com.oda.infrastructure.security.encryption.EncryptedLongConverter;
import com.oda.domain.user.EmploymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long userId;

    private LocalDate birthDate;
    private String sido;
    private String sigungu;

    @Convert(converter = EncryptedLongConverter.class)
    @Column(name = "personal_income_encrypted")
    private Long personalIncome;

    @Convert(converter = EncryptedLongConverter.class)
    @Column(name = "household_income_encrypted")
    private Long householdIncome;

    private Integer householdSize;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String targetJobCategories;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_profile_id")
    private List<EducationJpaEntity> educations = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_profile_id")
    private List<CertificationJpaEntity> certifications = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_profile_id")
    private List<WorkExperienceJpaEntity> workExperiences = new ArrayList<>();

    public static UserProfileJpaEntity create(Long userId) {
        UserProfileJpaEntity entity = new UserProfileJpaEntity();
        entity.userId = userId;
        return entity;
    }

    public void update(LocalDate birthDate, String sido, String sigungu,
                       Long personalIncome, Long householdIncome, Integer householdSize,
                       EmploymentStatus employmentStatus, String skills, String targetJobCategories,
                       List<EducationJpaEntity> educations,
                       List<CertificationJpaEntity> certifications,
                       List<WorkExperienceJpaEntity> workExperiences) {
        this.birthDate = birthDate;
        this.sido = sido;
        this.sigungu = sigungu;
        this.personalIncome = personalIncome;
        this.householdIncome = householdIncome;
        this.householdSize = householdSize;
        this.employmentStatus = employmentStatus;
        this.skills = skills;
        this.targetJobCategories = targetJobCategories;
        this.educations.clear();
        if (educations != null) this.educations.addAll(educations);
        this.certifications.clear();
        if (certifications != null) this.certifications.addAll(certifications);
        this.workExperiences.clear();
        if (workExperiences != null) this.workExperiences.addAll(workExperiences);
    }
}
