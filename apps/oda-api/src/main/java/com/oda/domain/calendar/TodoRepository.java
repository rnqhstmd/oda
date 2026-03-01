package com.oda.domain.calendar;

import com.oda.domain.calendar.Todo;
import com.oda.domain.calendar.TodoStatus;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    Todo save(Todo todo);
    Optional<Todo> findById(Long id);
    List<Todo> findByUserId(Long userId);
    List<Todo> findByUserIdAndStatus(Long userId, TodoStatus status);
    void deleteById(Long id);
}
