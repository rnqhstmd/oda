package com.oda.domain.policy;

import com.oda.domain.policy.EligibilityCriteria;
import com.oda.domain.policy.KoreanEligibilityParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KoreanEligibilityParserTest {

    @Test
    void 나이_범위_파싱() {
        String text = "만 19세 ~ 34세 청년";

        EligibilityCriteria criteria = KoreanEligibilityParser.parse(text);

        assertThat(criteria.minAge()).isEqualTo(19);
        assertThat(criteria.maxAge()).isEqualTo(34);
    }

    @Test
    void 소득_금액_파싱_콤마_포함() {
        String text = "1인 가구 2,564,238원 이하";

        EligibilityCriteria criteria = KoreanEligibilityParser.parse(text);

        assertThat(criteria.maxPersonalIncome()).isEqualTo(2_564_238L);
    }

    @Test
    void 지역_파싱() {
        String text = "서울, 경기 거주자 대상";

        EligibilityCriteria criteria = KoreanEligibilityParser.parse(text);

        assertThat(criteria.requiredRegions()).containsExactlyInAnyOrder("서울", "경기");
    }

    @Test
    void 복합_조건_파싱() {
        String text = "만 19세 ~ 34세이며 서울, 경기 거주자, 소득 3,000,000원 이하";

        EligibilityCriteria criteria = KoreanEligibilityParser.parse(text);

        assertThat(criteria.minAge()).isEqualTo(19);
        assertThat(criteria.maxAge()).isEqualTo(34);
        assertThat(criteria.maxPersonalIncome()).isEqualTo(3_000_000L);
        assertThat(criteria.requiredRegions()).containsExactlyInAnyOrder("서울", "경기");
    }

    @Test
    void 빈_텍스트_처리() {
        EligibilityCriteria criteria = KoreanEligibilityParser.parse("");

        assertThat(criteria.minAge()).isNull();
        assertThat(criteria.maxAge()).isNull();
        assertThat(criteria.maxPersonalIncome()).isNull();
        assertThat(criteria.requiredRegions()).isNull();
    }

    @Test
    void null_텍스트_처리() {
        EligibilityCriteria criteria = KoreanEligibilityParser.parse(null);

        assertThat(criteria.minAge()).isNull();
        assertThat(criteria.maxAge()).isNull();
    }

    @Test
    void 나이_조건_없으면_null() {
        String text = "소득 1,000,000원 이하";

        EligibilityCriteria criteria = KoreanEligibilityParser.parse(text);

        assertThat(criteria.minAge()).isNull();
        assertThat(criteria.maxAge()).isNull();
        assertThat(criteria.maxPersonalIncome()).isEqualTo(1_000_000L);
    }
}
