package com.oda.application.calendar.dto;

import com.oda.domain.calendar.RecurrenceRule;

import java.time.LocalDate;
import java.time.LocalTime;

public record AddEventCommand(
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime,
        boolean allDay,
        String actionUrl,
        RecurrenceRule recurrence
) {}
