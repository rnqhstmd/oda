package com.oda.domain.policy;

import com.oda.domain.policy.EligibilityCriteria;
import com.oda.domain.policy.MatchResult;
import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyCategory;
import com.oda.domain.policy.PolicyMatchingSpec;
import com.oda.domain.user.EmploymentStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PolicyMatchingSpecTest {

    private Policy buildPolicy(EligibilityCriteria criteria) {
        return Policy.create("테스트 정책", PolicyCategory.EMPLOYMENT, criteria);
    }

    @Test
    void 나이_범위_내_매칭() {
        EligibilityCriteria criteria = new EligibilityCriteria(
                19, 34, null, null, null, null, null, null);
        Policy policy = buildPolicy(criteria);

        MatchResult result = PolicyMatchingSpec.evaluate(policy, 25, null, null, null, null);

        assertThat(result.eligible()).isTrue();
    }

    @Test
    void 나이_초과_비매칭() {
        EligibilityCriteria criteria = new EligibilityCriteria(
                19, 34, null, null, null, null, null, null);
        Policy policy = buildPolicy(criteria);

        MatchResult result = PolicyMatchingSpec.evaluate(policy, 35, null, null, null, null);

        assertThat(result.eligible()).isFalse();
        assertThat(result.reason()).contains("나이");
    }

    @Test
    void 나이_미달_비매칭() {
        EligibilityCriteria criteria = new EligibilityCriteria(
                19, 34, null, null, null, null, null, null);
        Policy policy = buildPolicy(criteria);

        MatchResult result = PolicyMatchingSpec.evaluate(policy, 18, null, null, null, null);

        assertThat(result.eligible()).isFalse();
        assertThat(result.reason()).contains("나이");
    }

    @Test
    void 소득_초과_비매칭() {
        EligibilityCriteria criteria = new EligibilityCriteria(
                null, null, 2_000_000L, null, null, null, null, null);
        Policy policy = buildPolicy(criteria);

        MatchResult result = PolicyMatchingSpec.evaluate(policy, 25, 3_000_000L, null, null, null);

        assertThat(result.eligible()).isFalse();
        assertThat(result.reason()).contains("소득");
    }

    @Test
    void 지역_불일치_비매칭() {
        EligibilityCriteria criteria = new EligibilityCriteria(
                null, null, null, null, null, List.of("서울", "경기"), null, null);
        Policy policy = buildPolicy(criteria);

        MatchResult result = PolicyMatchingSpec.evaluate(policy, 25, null, null, "부산", null);

        assertThat(result.eligible()).isFalse();
        assertThat(result.reason()).contains("지역");
    }

    @Test
    void 취업상태_불일치_비매칭() {
        EligibilityCriteria criteria = new EligibilityCriteria(
                null, null, null, null, null, null,
                List.of(EmploymentStatus.UNEMPLOYED, EmploymentStatus.STUDENT), null);
        Policy policy = buildPolicy(criteria);

        MatchResult result = PolicyMatchingSpec.evaluate(policy, 25, null, null, null, EmploymentStatus.EMPLOYED);

        assertThat(result.eligible()).isFalse();
        assertThat(result.reason()).contains("취업 상태");
    }

    @Test
    void 모든_조건_충족_매칭() {
        EligibilityCriteria criteria = new EligibilityCriteria(
                19, 34,
                3_000_000L, 6_000_000L,
                null,
                List.of("서울", "경기"),
                List.of(EmploymentStatus.UNEMPLOYED),
                null);
        Policy policy = buildPolicy(criteria);

        MatchResult result = PolicyMatchingSpec.evaluate(
                policy, 25, 2_000_000L, 5_000_000L, "서울", EmploymentStatus.UNEMPLOYED);

        assertThat(result.eligible()).isTrue();
    }

    @Test
    void null_기준_무시() {
        EligibilityCriteria criteria = new EligibilityCriteria(
                null, null, null, null, null, null, null, null);
        Policy policy = buildPolicy(criteria);

        MatchResult result = PolicyMatchingSpec.evaluate(policy, 50, 10_000_000L, null, "제주", EmploymentStatus.EMPLOYED);

        assertThat(result.eligible()).isTrue();
    }

    @Test
    void criteria_자체가_null이면_항상_매칭() {
        Policy policy = Policy.create("무조건 정책", PolicyCategory.HOUSING, null);

        MatchResult result = PolicyMatchingSpec.evaluate(policy, 60, 50_000_000L, null, "제주", EmploymentStatus.SELF_EMPLOYED);

        assertThat(result.eligible()).isTrue();
    }
}
