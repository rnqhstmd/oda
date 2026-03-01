package com.oda.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValueObjectTest {

    @Test
    @DisplayName("Education record 동등성 검사")
    void education_equality() {
        Education e1 = new Education("서울대학교", "컴퓨터공학", "학사", true);
        Education e2 = new Education("서울대학교", "컴퓨터공학", "학사", true);
        assertThat(e1).isEqualTo(e2);
    }

    @Test
    @DisplayName("Education record 불변성 - 다른 값이면 다른 객체")
    void education_immutability_differentValues() {
        Education e1 = new Education("서울대학교", "컴퓨터공학", "학사", true);
        Education e2 = new Education("연세대학교", "경영학", "석사", false);
        assertThat(e1).isNotEqualTo(e2);
    }

    @Test
    @DisplayName("WorkExperience 유효한 날짜 범위")
    void workExperience_validDateRange() {
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2022, 12, 31);
        WorkExperience we = new WorkExperience("회사A", "개발자", start, end, false);
        assertThat(we.companyName()).isEqualTo("회사A");
        assertThat(we.isCurrent()).isFalse();
    }

    @Test
    @DisplayName("WorkExperience endDate가 startDate보다 이전이면 예외")
    void workExperience_invalidDateRange_throwsException() {
        LocalDate start = LocalDate.of(2022, 1, 1);
        LocalDate end = LocalDate.of(2020, 12, 31);
        assertThatThrownBy(() -> new WorkExperience("회사A", "개발자", start, end, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("endDate must be after startDate");
    }

    @Test
    @DisplayName("WorkExperience 재직중인 경우 endDate null 허용")
    void workExperience_currentJob_nullEndDate() {
        LocalDate start = LocalDate.of(2022, 1, 1);
        WorkExperience we = new WorkExperience("회사B", "매니저", start, null, true);
        assertThat(we.endDate()).isNull();
        assertThat(we.isCurrent()).isTrue();
    }

    @Test
    @DisplayName("Certification record 동등성 검사")
    void certification_equality() {
        LocalDate date = LocalDate.of(2023, 6, 15);
        Certification c1 = new Certification("정보처리기사", "한국산업인력공단", date);
        Certification c2 = new Certification("정보처리기사", "한국산업인력공단", date);
        assertThat(c1).isEqualTo(c2);
    }

    @Test
    @DisplayName("IncomeInfo 유효한 값")
    void incomeInfo_validValues() {
        IncomeInfo info = new IncomeInfo(30000000L, 60000000L, 3);
        assertThat(info.personalIncome()).isEqualTo(30000000L);
        assertThat(info.householdSize()).isEqualTo(3);
    }

    @Test
    @DisplayName("IncomeInfo personalIncome 음수이면 예외")
    void incomeInfo_negativePersonalIncome_throwsException() {
        assertThatThrownBy(() -> new IncomeInfo(-1L, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("personalIncome must be >= 0");
    }

    @Test
    @DisplayName("IncomeInfo householdIncome 음수이면 예외")
    void incomeInfo_negativeHouseholdIncome_throwsException() {
        assertThatThrownBy(() -> new IncomeInfo(null, -1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("householdIncome must be >= 0");
    }

    @Test
    @DisplayName("IncomeInfo householdSize 0이하면 예외")
    void incomeInfo_zeroHouseholdSize_throwsException() {
        assertThatThrownBy(() -> new IncomeInfo(null, null, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("householdSize must be >= 1");
    }

    @Test
    @DisplayName("OAuthUserInfo record 동등성 검사")
    void oauthUserInfo_equality() {
        OAuthUserInfo info1 = new OAuthUserInfo("id123", "user@example.com", "홍길동", "https://img.url");
        OAuthUserInfo info2 = new OAuthUserInfo("id123", "user@example.com", "홍길동", "https://img.url");
        assertThat(info1).isEqualTo(info2);
    }
}
