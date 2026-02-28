package com.oda.interfaces.api.calendar.dto;

import com.oda.application.calendar.dto.UpdateEventCommand;
import com.oda.domain.calendar.RecurrenceRule;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateEventRequest(
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime,
        boolean allDay,
        String actionUrl,
        RecurrenceRule recurrence
) {
    public UpdateEventCommand toCommand() {
        return new UpdateEventCommand(
                title, description, startDate, endDate,
                startTime, endTime, allDay, actionUrl, recurrence
        );
    }
}
