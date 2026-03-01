package com.oda.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserProfileTest {

    @Test
    @DisplayName("create_userId로_프로파일_생성")
    void create_userId로_프로파일_생성() {
        UserProfile profile = UserProfile.create(1L);

        assertThat(profile.getUserId()).isEqualTo(1L);
        assertThat(profile.getEducations()).isEmpty();
        assertThat(profile.getWorkExperiences()).isEmpty();
        assertThat(profile.getCertifications()).isEmpty();
        assertThat(profile.getSkills()).isEmpty();
        assertThat(profile.getTargetJobCategories()).isEmpty();
    }

    @Test
    @DisplayName("create_userId_null이면_예외")
    void create_userId_null이면_예외() {
        assertThatThrownBy(() -> UserProfile.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId must not be null");
    }

    @Test
    @DisplayName("update_프로파일_정보_업데이트")
    void update_프로파일_정보_업데이트() {
        UserProfile profile = UserProfile.create(1L);
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        List<Education> educations = List.of(new Education("서울대학교", "컴퓨터공학", "학사", true));
        List<String> skills = List.of("Java", "Spring");

        profile.update(birthDate, "서울", "강남구",
                EmploymentStatus.EMPLOYED,
                educations,
                List.of(),
                List.of(),
                skills,
                List.of("백엔드 개발자"));

        assertThat(profile.getBirthDate()).isEqualTo(birthDate);
        assertThat(profile.getSido()).isEqualTo("서울");
        assertThat(profile.getSigungu()).isEqualTo("강남구");
        assertThat(profile.getEmploymentStatus()).isEqualTo(EmploymentStatus.EMPLOYED);
        assertThat(profile.getEducations()).hasSize(1);
        assertThat(profile.getSkills()).containsExactly("Java", "Spring");
    }

    @Test
    @DisplayName("updateIncomeInfo_소득_정보_업데이트")
    void updateIncomeInfo_소득_정보_업데이트() {
        UserProfile profile = UserProfile.create(1L);
        IncomeInfo incomeInfo = new IncomeInfo(30000000L, 60000000L, 3);

        profile.updateIncomeInfo(incomeInfo);

        assertThat(profile.getIncomeInfo()).isEqualTo(incomeInfo);
        assertThat(profile.getIncomeInfo().personalIncome()).isEqualTo(30000000L);
    }

    @Test
    @DisplayName("calculateAge_나이_계산")
    void calculateAge_나이_계산() {
        UserProfile profile = UserProfile.create(1L);
        profile.update(LocalDate.of(1990, 1, 1), null, null,
                null, null, null, null, null, null);

        int age = profile.calculateAge();

        assertThat(age).isGreaterThanOrEqualTo(35);
    }

    @Test
    @DisplayName("calculateAge_birthDate_없으면_예외")
    void calculateAge_birthDate_없으면_예외() {
        UserProfile profile = UserProfile.create(1L);

        assertThatThrownBy(profile::calculateAge)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("birthDate is not set");
    }
}
