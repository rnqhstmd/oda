package com.oda.domain.calendar;

import com.oda.domain.common.BaseEntity;
import com.oda.domain.job.JobPosting;
import com.oda.domain.policy.Policy;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class CalendarEvent extends BaseEntity {

    private Long id;
    private Long userId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private EventType type;
    private EventSource source;
    private String actionUrl;
    private boolean allDay;
    private RecurrenceRule recurrence;

    private CalendarEvent() {}

    public static CalendarEvent fromPolicy(Policy policy, Long userId) {
        CalendarEvent event = new CalendarEvent();
        event.userId = userId;
        event.title = policy.getTitle() + " 마감";
        event.description = policy.getSummary();
        event.startDate = policy.getApplicationEndDate();
        event.endDate = policy.getApplicationEndDate();
        event.type = EventType.POLICY;
        event.source = new EventSource("POLICY", policy.getId());
        event.actionUrl = policy.getApplicationUrl();
        event.allDay = true;
        return event;
    }

    public static CalendarEvent fromJobPosting(JobPosting job, Long userId) {
        CalendarEvent event = new CalendarEvent();
        event.userId = userId;
        event.title = job.getTitle() + " 마감";
        event.description = job.getDescription();
        event.startDate = job.getApplicationDeadline();
        event.endDate = job.getApplicationDeadline();
        event.type = EventType.JOB;
        event.source = new EventSource("JOB", job.getId());
        event.actionUrl = job.getApplicationUrl();
        event.allDay = true;
        return event;
    }

    public static CalendarEvent createCustom(Long userId, String title, String description,
                                              LocalDate startDate, LocalDate endDate,
                                              LocalTime startTime, LocalTime endTime,
                                              boolean allDay, String actionUrl,
                                              RecurrenceRule recurrence) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("startDate must not be null");
        }
        CalendarEvent event = new CalendarEvent();
        event.userId = userId;
        event.title = title;
        event.description = description;
        event.startDate = startDate;
        event.endDate = endDate;
        event.startTime = startTime;
        event.endTime = endTime;
        event.type = EventType.CUSTOM;
        event.allDay = allDay;
        event.actionUrl = actionUrl;
        event.recurrence = recurrence;
        return event;
    }

    public static CalendarEvent reconstruct(Long id, Long userId, String title, String description,
                                             LocalDate startDate, LocalDate endDate,
                                             LocalTime startTime, LocalTime endTime,
                                             EventType type, EventSource source, String actionUrl,
                                             boolean allDay, RecurrenceRule recurrence) {
        CalendarEvent event = new CalendarEvent();
        event.id = id;
        event.userId = userId;
        event.title = title;
        event.description = description;
        event.startDate = startDate;
        event.endDate = endDate;
        event.startTime = startTime;
        event.endTime = endTime;
        event.type = type;
        event.source = source;
        event.actionUrl = actionUrl;
        event.allDay = allDay;
        event.recurrence = recurrence;
        return event;
    }

    public void update(String title, String description, LocalDate startDate, LocalDate endDate,
                       LocalTime startTime, LocalTime endTime, boolean allDay,
                       String actionUrl, RecurrenceRule recurrence) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        this.description = description;
        if (startDate != null) {
            this.startDate = startDate;
        }
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.allDay = allDay;
        this.actionUrl = actionUrl;
        this.recurrence = recurrence;
    }
}
