package com.oda.application.calendar.dto;

import com.oda.domain.calendar.CalendarEvent;
import com.oda.domain.calendar.EventSource;
import com.oda.domain.calendar.EventType;
import com.oda.domain.calendar.RecurrenceRule;

import java.time.LocalDate;
import java.time.LocalTime;

public record CalendarEventResult(
        Long id,
        Long userId,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime,
        EventType type,
        EventSource source,
        String actionUrl,
        boolean allDay,
        RecurrenceRule recurrence
) {
    public static CalendarEventResult from(CalendarEvent event) {
        return new CalendarEventResult(
                event.getId(),
                event.getUserId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getType(),
                event.getSource(),
                event.getActionUrl(),
                event.isAllDay(),
                event.getRecurrence()
        );
    }
}
