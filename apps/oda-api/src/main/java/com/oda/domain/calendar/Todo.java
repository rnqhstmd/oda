package com.oda.domain.calendar;

import com.oda.domain.common.BaseEntity;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class Todo extends BaseEntity {

    private Long id;
    private Long userId;
    private Long calendarEventId;
    private String title;
    private String description;
    private TodoPriority priority;
    private TodoStatus status;
    private LocalDate dueDate;
    private LocalDateTime completedAt;

    private Todo() {}

    public static Todo create(Long userId, String title, TodoPriority priority, LocalDate dueDate) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (priority == null) {
            throw new IllegalArgumentException("priority must not be null");
        }
        Todo todo = new Todo();
        todo.userId = userId;
        todo.title = title;
        todo.priority = priority;
        todo.dueDate = dueDate;
        todo.status = TodoStatus.PENDING;
        return todo;
    }

    public static Todo reconstruct(Long id, Long userId, Long calendarEventId, String title,
                                    String description, TodoPriority priority, TodoStatus status,
                                    LocalDate dueDate, LocalDateTime completedAt) {
        Todo todo = new Todo();
        todo.id = id;
        todo.userId = userId;
        todo.calendarEventId = calendarEventId;
        todo.title = title;
        todo.description = description;
        todo.priority = priority;
        todo.status = status;
        todo.dueDate = dueDate;
        todo.completedAt = completedAt;
        return todo;
    }

    public void start() {
        if (status != TodoStatus.PENDING) {
            throw new IllegalStateException("Todo can only be started from PENDING status, current: " + status);
        }
        this.status = TodoStatus.IN_PROGRESS;
    }

    public void complete() {
        if (status == TodoStatus.COMPLETED) {
            throw new IllegalStateException("Todo is already completed");
        }
        this.status = TodoStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void update(String title, String description, TodoPriority priority, LocalDate dueDate) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        this.description = description;
        if (priority != null) {
            this.priority = priority;
        }
        this.dueDate = dueDate;
    }

    public void linkToCalendarEvent(Long calendarEventId) {
        this.calendarEventId = calendarEventId;
    }
}
