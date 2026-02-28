package com.oda.domain.calendar;

import com.oda.domain.calendar.CalendarEvent;
import com.oda.domain.calendar.EventSource;
import com.oda.domain.calendar.EventType;
import com.oda.domain.job.Company;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;
import com.oda.domain.policy.EligibilityCriteria;
import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyCategory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalendarEventTest {

    private Policy createPolicy() {
        Policy policy = Policy.create("청년 취업 지원금", PolicyCategory.EMPLOYMENT,
                new EligibilityCriteria(19, 34, null, null, null, null, null, null));
        policy.update("청년 취업 지원금", "요약", "설명", "고용노동부", null,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "http://policy.go.kr");
        return policy;
    }

    private JobPosting createJob() {
        Company company = new Company("카카오", "IT", "대기업", "서울");
        JobPosting job = JobPosting.create("백엔드 개발자", company, JobSource.WANTED);
        job.update("EXT-001", "백엔드 개발자 모집", null, 3, "학사",
                "5000만원", "서울", "정규직", "http://wanted.co.kr/apply",
                LocalDate.of(2025, 12, 31));
        return job;
    }

    @Test
    void fromPolicy_정책에서_이벤트_생성() {
        Policy policy = createPolicy();
        Long userId = 1L;

        CalendarEvent event = CalendarEvent.fromPolicy(policy, userId);

        assertThat(event.getTitle()).contains("마감");
        assertThat(event.getType()).isEqualTo(EventType.POLICY);
        assertThat(event.getUserId()).isEqualTo(userId);
        assertThat(event.isAllDay()).isTrue();
        assertThat(event.getSource()).isNotNull();
        assertThat(event.getSource().sourceType()).isEqualTo("POLICY");
    }

    @Test
    void fromJobPosting_채용에서_이벤트_생성() {
        JobPosting job = createJob();
        Long userId = 2L;

        CalendarEvent event = CalendarEvent.fromJobPosting(job, userId);

        assertThat(event.getTitle()).contains("마감");
        assertThat(event.getType()).isEqualTo(EventType.JOB);
        assertThat(event.getUserId()).isEqualTo(userId);
        assertThat(event.isAllDay()).isTrue();
        assertThat(event.getSource()).isNotNull();
        assertThat(event.getSource().sourceType()).isEqualTo("JOB");
    }

    @Test
    void createCustom_커스텀_이벤트_생성() {
        CalendarEvent event = CalendarEvent.createCustom(
                1L, "스터디 모임", "알고리즘 스터디",
                LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 15),
                LocalTime.of(14, 0), LocalTime.of(16, 0),
                false, null, null
        );

        assertThat(event.getTitle()).isEqualTo("스터디 모임");
        assertThat(event.getType()).isEqualTo(EventType.CUSTOM);
        assertThat(event.isAllDay()).isFalse();
        assertThat(event.getStartTime()).isEqualTo(LocalTime.of(14, 0));
    }

    @Test
    void update_이벤트_수정() {
        CalendarEvent event = CalendarEvent.createCustom(
                1L, "원래 제목", null,
                LocalDate.of(2025, 6, 15), null,
                null, null, true, null, null
        );

        event.update("수정된 제목", "새 설명", LocalDate.of(2025, 7, 1),
                LocalDate.of(2025, 7, 1), null, null, false, "http://new.url", null);

        assertThat(event.getTitle()).isEqualTo("수정된 제목");
        assertThat(event.getDescription()).isEqualTo("새 설명");
        assertThat(event.getStartDate()).isEqualTo(LocalDate.of(2025, 7, 1));
        assertThat(event.isAllDay()).isFalse();
    }

    @Test
    void fromPolicy_마감일_자동설정() {
        Policy policy = createPolicy();
        Long userId = 1L;

        CalendarEvent event = CalendarEvent.fromPolicy(policy, userId);

        assertThat(event.getStartDate()).isEqualTo(policy.getApplicationEndDate());
        assertThat(event.getEndDate()).isEqualTo(policy.getApplicationEndDate());
        assertThat(event.getActionUrl()).isEqualTo(policy.getApplicationUrl());
    }

    @Test
    void createCustom_제목_없으면_예외() {
        assertThatThrownBy(() -> CalendarEvent.createCustom(
                1L, "", null, LocalDate.now(), null, null, null, true, null, null
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("title");
    }

    @Test
    void createCustom_시작일_없으면_예외() {
        assertThatThrownBy(() -> CalendarEvent.createCustom(
                1L, "제목", null, null, null, null, null, true, null, null
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("startDate");
    }
}
