package com.oda.infrastructure.calendar.persistence;

import com.oda.domain.calendar.Todo;
import com.oda.domain.calendar.TodoStatus;
import com.oda.domain.calendar.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaTodoRepository implements TodoRepository {

    private final SpringDataTodoRepository springDataRepository;
    private final TodoMapper mapper;

    @Override
    public Todo save(Todo todo) {
        TodoJpaEntity entity = mapper.toEntity(todo);
        TodoJpaEntity saved = springDataRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Todo> findByUserId(Long userId) {
        return springDataRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Todo> findByUserIdAndStatus(Long userId, TodoStatus status) {
        return springDataRepository.findByUserIdAndStatus(userId, status).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }
}
