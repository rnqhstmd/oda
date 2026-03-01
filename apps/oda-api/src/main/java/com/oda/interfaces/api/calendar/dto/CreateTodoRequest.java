package com.oda.interfaces.api.calendar.dto;

import com.oda.application.calendar.dto.CreateTodoCommand;
import com.oda.domain.calendar.TodoPriority;

import java.time.LocalDate;

public record CreateTodoRequest(
        String title,
        String description,
        TodoPriority priority,
        LocalDate dueDate
) {
    public CreateTodoCommand toCommand() {
        return new CreateTodoCommand(title, description, priority, dueDate);
    }
}
