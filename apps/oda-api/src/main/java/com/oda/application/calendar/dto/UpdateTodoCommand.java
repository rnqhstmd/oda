package com.oda.application.calendar.dto;

import com.oda.domain.calendar.TodoPriority;

import java.time.LocalDate;

public record UpdateTodoCommand(
        String title,
        String description,
        TodoPriority priority,
        LocalDate dueDate
) {}
