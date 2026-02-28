package com.oda.interfaces.api.calendar.dto;

import com.oda.application.calendar.dto.AddEventCommand;
import com.oda.domain.calendar.RecurrenceRule;

import java.time.LocalDate;
import java.time.LocalTime;

public record AddEventRequest(
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
    public AddEventCommand toCommand() {
        return new AddEventCommand(
                title, description, startDate, endDate,
                startTime, endTime, allDay, actionUrl, recurrence
        );
    }
}
