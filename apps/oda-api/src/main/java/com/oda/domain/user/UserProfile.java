package com.oda.domain.user;

import com.oda.domain.common.BaseEntity;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Getter
public class UserProfile extends BaseEntity {
    private Long id;
    private Long userId;
    private LocalDate birthDate;
    private String sido;
    private String sigungu;
    private IncomeInfo incomeInfo;
    private EmploymentStatus employmentStatus;
    private List<Education> educations;
    private List<WorkExperience> workExperiences;
    private List<Certification> certifications;
    private List<String> skills;
    private List<String> targetJobCategories;

    private UserProfile() {}

    public static UserProfile reconstruct(Long id, Long userId, LocalDate birthDate, String sido,
                                          String sigungu, IncomeInfo incomeInfo,
                                          EmploymentStatus employmentStatus,
                                          List<Education> educations,
                                          List<WorkExperience> workExperiences,
                                          List<Certification> certifications,
                                          List<String> skills, List<String> targetJobCategories) {
        UserProfile profile = new UserProfile();
        profile.id = id;
        profile.userId = userId;
        profile.birthDate = birthDate;
        profile.sido = sido;
        profile.sigungu = sigungu;
        profile.incomeInfo = incomeInfo;
        profile.employmentStatus = employmentStatus;
        profile.educations = educations != null ? new ArrayList<>(educations) : new ArrayList<>();
        profile.workExperiences = workExperiences != null ? new ArrayList<>(workExperiences) : new ArrayList<>();
        profile.certifications = certifications != null ? new ArrayList<>(certifications) : new ArrayList<>();
        profile.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>();
        profile.targetJobCategories = targetJobCategories != null ? new ArrayList<>(targetJobCategories) : new ArrayList<>();
        return profile;
    }

    public static UserProfile create(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        UserProfile profile = new UserProfile();
        profile.userId = userId;
        profile.educations = new ArrayList<>();
        profile.workExperiences = new ArrayList<>();
        profile.certifications = new ArrayList<>();
        profile.skills = new ArrayList<>();
        profile.targetJobCategories = new ArrayList<>();
        return profile;
    }

    public void update(LocalDate birthDate, String sido, String sigungu,
                       EmploymentStatus employmentStatus,
                       List<Education> educations,
                       List<WorkExperience> workExperiences,
                       List<Certification> certifications,
                       List<String> skills,
                       List<String> targetJobCategories) {
        this.birthDate = birthDate;
        this.sido = sido;
        this.sigungu = sigungu;
        this.employmentStatus = employmentStatus;
        if (educations != null) this.educations = new ArrayList<>(educations);
        if (workExperiences != null) this.workExperiences = new ArrayList<>(workExperiences);
        if (certifications != null) this.certifications = new ArrayList<>(certifications);
        if (skills != null) this.skills = new ArrayList<>(skills);
        if (targetJobCategories != null) this.targetJobCategories = new ArrayList<>(targetJobCategories);
    }

    public void updateIncomeInfo(IncomeInfo incomeInfo) {
        this.incomeInfo = incomeInfo;
    }

    public Long getPersonalIncome() {
        return incomeInfo != null ? incomeInfo.personalIncome() : null;
    }

    public Long getHouseholdIncome() {
        return incomeInfo != null ? incomeInfo.householdIncome() : null;
    }

    public int calculateAge() {
        if (birthDate == null) {
            throw new IllegalStateException("birthDate is not set");
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public int calculateTotalExperienceYears() {
        if (workExperiences == null) return 0;
        return workExperiences.stream()
                .mapToInt(we -> {
                    LocalDate start = we.startDate();
                    if (start == null) return 0;
                    LocalDate end = we.isCurrent() || we.endDate() == null ? LocalDate.now() : we.endDate();
                    return Math.max(0, Period.between(start, end).getYears());
                })
                .sum();
    }

    public String resolveHighestEducation() {
        if (educations == null || educations.isEmpty()) return null;
        return educations.stream()
                .map(Education::degree)
                .findFirst()
                .orElse(null);
    }
}
