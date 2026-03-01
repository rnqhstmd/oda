package com.oda.domain.policy;

import com.oda.domain.policy.EligibilityCriteria;
import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyCategory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PolicyTest {

    @Test
    void create_정상생성() {
        EligibilityCriteria criteria = new EligibilityCriteria(
                19, 34, null, null, null, null, null, null);

        Policy policy = Policy.create("청년 취업 지원금", PolicyCategory.EMPLOYMENT, criteria);

        assertThat(policy.getTitle()).isEqualTo("청년 취업 지원금");
        assertThat(policy.getCategory()).isEqualTo(PolicyCategory.EMPLOYMENT);
        assertThat(policy.getEligibility()).isEqualTo(criteria);
        assertThat(policy.isActive()).isTrue();
    }

    @Test
    void create_제목_없으면_예외() {
        assertThatThrownBy(() -> Policy.create("", PolicyCategory.EMPLOYMENT, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("title");
    }

    @Test
    void create_카테고리_없으면_예외() {
        assertThatThrownBy(() -> Policy.create("청년 정책", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("category");
    }

    @Test
    void update_제목_및_정보_갱신() {
        Policy policy = Policy.create("원래 제목", PolicyCategory.HOUSING, null);

        policy.update("새 제목", "요약", "설명", "기관명", null,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "http://example.com");

        assertThat(policy.getTitle()).isEqualTo("새 제목");
        assertThat(policy.getSummary()).isEqualTo("요약");
        assertThat(policy.getOrganizationName()).isEqualTo("기관명");
    }

    @Test
    void isApplicationOpen_마감일이_오늘_이후이면_true() {
        Policy policy = Policy.create("정책", PolicyCategory.FINANCE, null);
        policy.update(null, null, null, null, null,
                LocalDate.now().minusDays(10), LocalDate.now().plusDays(10), null);

        assertThat(policy.isApplicationOpen()).isTrue();
    }

    @Test
    void isApplicationOpen_마감일이_과거이면_false() {
        Policy policy = Policy.create("정책", PolicyCategory.FINANCE, null);
        policy.update(null, null, null, null, null,
                LocalDate.now().minusDays(30), LocalDate.now().minusDays(1), null);

        assertThat(policy.isApplicationOpen()).isFalse();
    }

    @Test
    void isApplicationOpen_마감일이_오늘이면_true() {
        Policy policy = Policy.create("정책", PolicyCategory.EDUCATION, null);
        policy.update(null, null, null, null, null,
                LocalDate.now().minusDays(1), LocalDate.now(), null);

        assertThat(policy.isApplicationOpen()).isTrue();
    }

    @Test
    void isApplicationOpen_마감일_없으면_false() {
        Policy policy = Policy.create("정책", PolicyCategory.STARTUP, null);

        assertThat(policy.isApplicationOpen()).isFalse();
    }
}
