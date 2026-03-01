package com.oda.application.notification;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NudgeMessageGeneratorTest {

    @Test
    void D_day_메시지_긴급() {
        String title = NudgeMessageGenerator.generateTitle("청년 취업 지원금", 0);
        String body = NudgeMessageGenerator.generateBody("청년 취업 지원금", 0, null);

        assertThat(title).contains("오늘이 마감일입니다!");
        assertThat(body).contains("놓치면 다음 기회는 6개월 뒤입니다");
    }

    @Test
    void D_1_손실회피_메시지() {
        String title = NudgeMessageGenerator.generateTitle("청년 주거 지원", 1);
        String body = NudgeMessageGenerator.generateBody("청년 주거 지원", 1, null);

        assertThat(title).contains("내일 마감!");
        assertThat(body).contains("놓치면 다음 기회는 6개월 뒤입니다");
    }

    @Test
    void D_3_준비_메시지() {
        String title = NudgeMessageGenerator.generateTitle("창업 지원금", 3);
        String body = NudgeMessageGenerator.generateBody("창업 지원금", 3, null);

        assertThat(title).contains("D-3");
        assertThat(body).contains("마감 3일 전");
        assertThat(body).contains("필요 서류를 확인하세요");
    }

    @Test
    void D_7_일반_메시지() {
        String title = NudgeMessageGenerator.generateTitle("교육 지원금", 7);
        String body = NudgeMessageGenerator.generateBody("교육 지원금", 7, null);

        assertThat(title).contains("D-7");
        assertThat(body).contains("마감 7일 전입니다");
    }
}
