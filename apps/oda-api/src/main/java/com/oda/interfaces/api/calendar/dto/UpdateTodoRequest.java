package com.oda.interfaces.api.calendar.dto;

import com.oda.application.calendar.dto.UpdateTodoCommand;
import com.oda.domain.calendar.TodoPriority;

import java.time.LocalDate;

public record UpdateTodoRequest(
        String title,
        String description,
        TodoPriority priority,
        LocalDate dueDate
) {
    public UpdateTodoCommand toCommand() {
        return new UpdateTodoCommand(title, description, priority, dueDate);
    }
}
