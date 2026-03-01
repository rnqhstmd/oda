package com.oda.infrastructure.calendar.persistence;

import com.oda.domain.calendar.Todo;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {

    public Todo toDomain(TodoJpaEntity entity) {
        return Todo.reconstruct(
                entity.getId(),
                entity.getUserId(),
                entity.getCalendarEventId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPriority(),
                entity.getStatus(),
                entity.getDueDate(),
                entity.getCompletedAt()
        );
    }

    public TodoJpaEntity toEntity(Todo domain) {
        if (domain.getId() != null) {
            return TodoJpaEntity.withId(
                    domain.getId(),
                    domain.getUserId(),
                    domain.getCalendarEventId(),
                    domain.getTitle(),
                    domain.getDescription(),
                    domain.getPriority(),
                    domain.getStatus(),
                    domain.getDueDate(),
                    domain.getCompletedAt()
            );
        }
        return TodoJpaEntity.create(
                domain.getUserId(),
                domain.getCalendarEventId(),
                domain.getTitle(),
                domain.getDescription(),
                domain.getPriority(),
                domain.getStatus(),
                domain.getDueDate(),
                domain.getCompletedAt()
        );
    }
}
