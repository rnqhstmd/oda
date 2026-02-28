package com.oda.application.calendar.dto;

import com.oda.domain.calendar.Todo;
import com.oda.domain.calendar.TodoPriority;
import com.oda.domain.calendar.TodoStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TodoResult(
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
    public static TodoResult from(Todo todo) {
        return new TodoResult(
                todo.getId(),
                todo.getUserId(),
                todo.getCalendarEventId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getPriority(),
                todo.getStatus(),
                todo.getDueDate(),
                todo.getCompletedAt()
        );
    }
}
