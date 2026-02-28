package com.oda.interfaces.api.calendar.dto;

import com.oda.application.calendar.dto.CalendarEventResult;
import com.oda.domain.calendar.EventSource;
import com.oda.domain.calendar.EventType;
import com.oda.domain.calendar.RecurrenceRule;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventResponse(
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
    public static EventResponse from(CalendarEventResult result) {
        return new EventResponse(
                result.id(),
                result.userId(),
                result.title(),
                result.description(),
                result.startDate(),
                result.endDate(),
                result.startTime(),
                result.endTime(),
                result.type(),
                result.source(),
                result.actionUrl(),
                result.allDay(),
                result.recurrence()
        );
    }
}
