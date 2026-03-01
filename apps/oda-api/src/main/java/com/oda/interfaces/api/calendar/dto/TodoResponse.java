package com.oda.interfaces.api.calendar.dto;

import com.oda.application.calendar.dto.TodoResult;
import com.oda.domain.calendar.TodoPriority;
import com.oda.domain.calendar.TodoStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TodoResponse(
        Long id,
        Long userId,
        Long calendarEventId,
        String title,
        String description,
        TodoPriority priority,
        TodoStatus status,
        LocalDate dueDate,
        LocalDateTime completedAt
) {
    public static TodoResponse from(TodoResult result) {
        return new TodoResponse(
                result.id(),
                result.userId(),
                result.calendarEventId(),
                result.title(),
                result.description(),
                result.priority(),
                result.status(),
                result.dueDate(),
                result.completedAt()
        );
    }
}
