package com.oda.application.calendar;

import com.oda.application.calendar.dto.*;
import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.calendar.CalendarEvent;
import com.oda.domain.calendar.EventType;
import com.oda.domain.calendar.CalendarEventRepository;
import com.oda.domain.job.Company;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;
import com.oda.domain.job.JobPostingRepository;
import com.oda.domain.policy.EligibilityCriteria;
import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyCategory;
import com.oda.domain.policy.PolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CalendarEventServiceTest {

    @Mock
    private CalendarEventRepository calendarEventRepository;

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @InjectMocks
    private CalendarEventService calendarEventService;

    private Policy createPolicy(Long id) {
        Policy policy = Policy.create("청년 취업 지원금", PolicyCategory.EMPLOYMENT,
                new EligibilityCriteria(19, 34, null, null, null, null, null, null));
        policy.update("청년 취업 지원금", "요약", "설명", "고용노동부", null,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "http://policy.go.kr");
        ReflectionTestUtils.setField(policy, "id", id);
        return policy;
    }

    private JobPosting createJob(Long id) {
        Company company = new Company("카카오", "IT", "대기업", "서울");
        JobPosting job = JobPosting.create("백엔드 개발자", company, JobSource.WANTED);
        job.update("EXT-001", "백엔드 개발자 모집", null, 3, "학사",
                "5000만원", "서울", "정규직", "http://wanted.co.kr/apply",
                LocalDate.of(2025, 12, 31));
        ReflectionTestUtils.setField(job, "id", id);
        return job;
    }

    private CalendarEvent createSavedEvent(Long id, Long userId) {
        CalendarEvent event = CalendarEvent.createCustom(userId, "테스트 이벤트", null,
                LocalDate.of(2025, 6, 15), null, null, null, true, null, null);
        ReflectionTestUtils.setField(event, "id", id);
        return event;
    }

    @Test
    void addEvent_커스텀_이벤트_추가() {
        Long userId = 1L;
        AddEventCommand command = new AddEventCommand(
                "스터디 모임", "알고리즘", LocalDate.of(2025, 6, 15),
                null, null, null, true, null, null
        );
        CalendarEvent saved = createSavedEvent(1L, userId);
        given(calendarEventRepository.save(any())).willReturn(saved);

        CalendarEventResult result = calendarEventService.addEvent(userId, command);

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        verify(calendarEventRepository).save(any());
    }

    @Test
    void addFromPolicy_정책에서_이벤트_추가() {
        Long userId = 1L;
        Long policyId = 10L;
        Policy policy = createPolicy(policyId);
        AddEventFromPolicyCommand command = new AddEventFromPolicyCommand(policyId);

        CalendarEvent saved = CalendarEvent.fromPolicy(policy, userId);
        ReflectionTestUtils.setField(saved, "id", 1L);

        given(policyRepository.findById(policyId)).willReturn(Optional.of(policy));
        given(calendarEventRepository.save(any())).willReturn(saved);

        CalendarEventResult result = calendarEventService.addFromPolicy(userId, command);

        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(EventType.POLICY);
        verify(policyRepository).findById(policyId);
    }

    @Test
    void addFromJob_채용에서_이벤트_추가() {
        Long userId = 1L;
        Long jobId = 20L;
        JobPosting job = createJob(jobId);
        AddEventFromJobCommand command = new AddEventFromJobCommand(jobId);

        CalendarEvent saved = CalendarEvent.fromJobPosting(job, userId);
        ReflectionTestUtils.setField(saved, "id", 1L);

        given(jobPostingRepository.findById(jobId)).willReturn(Optional.of(job));
        given(calendarEventRepository.save(any())).willReturn(saved);

        CalendarEventResult result = calendarEventService.addFromJob(userId, command);

        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(EventType.JOB);
        verify(jobPostingRepository).findById(jobId);
    }

    @Test
    void getEvents_기간별_이벤트_조회() {
        Long userId = 1L;
        LocalDate start = LocalDate.of(2025, 6, 1);
        LocalDate end = LocalDate.of(2025, 6, 30);
        CalendarEvent event = createSavedEvent(1L, userId);

        given(calendarEventRepository.findByUserIdAndStartDateBetween(userId, start, end))
                .willReturn(List.of(event));

        List<CalendarEventResult> results = calendarEventService.getEvents(userId, start, end);

        assertThat(results).hasSize(1);
        verify(calendarEventRepository).findByUserIdAndStartDateBetween(userId, start, end);
    }

    @Test
    void getEvent_단건_조회() {
        Long eventId = 1L;
        CalendarEvent event = createSavedEvent(eventId, 1L);

        given(calendarEventRepository.findById(eventId)).willReturn(Optional.of(event));

        CalendarEventResult result = calendarEventService.getEvent(eventId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(eventId);
    }

    @Test
    void getEvent_존재하지_않으면_예외() {
        Long eventId = 999L;
        given(calendarEventRepository.findById(eventId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> calendarEventService.getEvent(eventId))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> org.assertj.core.api.Assertions.assertThat(((CoreException) e).getErrorType()).isEqualTo(ErrorType.NOT_FOUND));
    }

    @Test
    void deleteEvent_이벤트_삭제() {
        Long eventId = 1L;
        CalendarEvent event = createSavedEvent(eventId, 1L);
        given(calendarEventRepository.findById(eventId)).willReturn(Optional.of(event));

        calendarEventService.deleteEvent(eventId);

        verify(calendarEventRepository).deleteById(eventId);
    }
}
