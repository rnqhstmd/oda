package com.oda.infrastructure.calendar.persistence;

import com.oda.domain.calendar.TodoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataTodoRepository extends JpaRepository<TodoJpaEntity, Long> {
    List<TodoJpaEntity> findByUserId(Long userId);
    List<TodoJpaEntity> findByUserIdAndStatus(Long userId, TodoStatus status);
}
