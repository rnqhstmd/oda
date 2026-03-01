package com.oda.infrastructure.calendar.persistence;

import com.oda.domain.calendar.TodoPriority;
import com.oda.domain.calendar.TodoStatus;
import com.oda.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "todos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private Long calendarEventId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private TodoPriority priority;

    @Enumerated(EnumType.STRING)
    private TodoStatus status;

    private LocalDate dueDate;

    private LocalDateTime completedAt;

    public static TodoJpaEntity create(
            Long userId, Long calendarEventId, String title, String description,
            TodoPriority priority, TodoStatus status, LocalDate dueDate, LocalDateTime completedAt) {
        TodoJpaEntity entity = new TodoJpaEntity();
        entity.userId = userId;
        entity.calendarEventId = calendarEventId;
        entity.title = title;
        entity.description = description;
        entity.priority = priority;
        entity.status = status;
        entity.dueDate = dueDate;
        entity.completedAt = completedAt;
        return entity;
    }

    public static TodoJpaEntity withId(
            Long id, Long userId, Long calendarEventId, String title, String description,
            TodoPriority priority, TodoStatus status, LocalDate dueDate, LocalDateTime completedAt) {
        TodoJpaEntity entity = create(userId, calendarEventId, title, description,
                priority, status, dueDate, completedAt);
        entity.id = id;
        return entity;
    }
}
