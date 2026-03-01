package com.oda.application.calendar;

import com.oda.application.calendar.dto.*;
import com.oda.domain.calendar.CalendarEvent;
import com.oda.domain.calendar.CalendarEventRepository;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobPostingRepository;
import com.oda.domain.policy.Policy;
import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.policy.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarEventService {

    private final CalendarEventRepository calendarEventRepository;
    private final PolicyRepository policyRepository;
    private final JobPostingRepository jobPostingRepository;

    public CalendarEventResult addEvent(Long userId, AddEventCommand command) {
        CalendarEvent event = CalendarEvent.createCustom(
                userId,
                command.title(),
                command.description(),
                command.startDate(),
                command.endDate(),
                command.startTime(),
                command.endTime(),
                command.allDay(),
                command.actionUrl(),
                command.recurrence()
        );
        CalendarEvent saved = calendarEventRepository.save(event);
        return CalendarEventResult.from(saved);
    }

    public CalendarEventResult addFromPolicy(Long userId, AddEventFromPolicyCommand command) {
        Policy policy = policyRepository.findById(command.policyId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Policy not found with id: " + command.policyId()));
        CalendarEvent event = CalendarEvent.fromPolicy(policy, userId);
        CalendarEvent saved = calendarEventRepository.save(event);
        return CalendarEventResult.from(saved);
    }

    public CalendarEventResult addFromJob(Long userId, AddEventFromJobCommand command) {
        JobPosting job = jobPostingRepository.findById(command.jobPostingId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Job not found with id: " + command.jobPostingId()));
        CalendarEvent event = CalendarEvent.fromJobPosting(job, userId);
        CalendarEvent saved = calendarEventRepository.save(event);
        return CalendarEventResult.from(saved);
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResult> getEvents(Long userId, LocalDate start, LocalDate end) {
        return calendarEventRepository.findByUserIdAndStartDateBetween(userId, start, end)
                .stream()
                .map(CalendarEventResult::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CalendarEventResult getEvent(Long eventId) {
        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "CalendarEvent not found: " + eventId));
        return CalendarEventResult.from(event);
    }

    public void updateEvent(Long eventId, UpdateEventCommand command) {
        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "CalendarEvent not found: " + eventId));
        event.update(
                command.title(),
                command.description(),
                command.startDate(),
                command.endDate(),
                command.startTime(),
                command.endTime(),
                command.allDay(),
                command.actionUrl(),
                command.recurrence()
        );
        calendarEventRepository.save(event);
    }

    public void deleteEvent(Long eventId) {
        calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "CalendarEvent not found: " + eventId));
        calendarEventRepository.deleteById(eventId);
    }
}
