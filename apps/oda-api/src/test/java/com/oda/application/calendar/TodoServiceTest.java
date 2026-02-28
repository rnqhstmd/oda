package com.oda.application.calendar;

import com.oda.application.calendar.dto.CreateTodoCommand;
import com.oda.application.calendar.dto.TodoResult;
import com.oda.application.calendar.dto.UpdateTodoCommand;
import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.calendar.Todo;
import com.oda.domain.calendar.TodoPriority;
import com.oda.domain.calendar.TodoStatus;
import com.oda.domain.calendar.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo createSavedTodo(Long id, Long userId, TodoStatus status) {
        Todo todo = Todo.create(userId, "테스트 할일", TodoPriority.MEDIUM, LocalDate.of(2025, 12, 31));
        ReflectionTestUtils.setField(todo, "id", id);
        if (status == TodoStatus.IN_PROGRESS) {
            todo.start();
        } else if (status == TodoStatus.COMPLETED) {
            todo.complete();
        }
        return todo;
    }

    @Test
    void createTodo_할일_생성() {
        Long userId = 1L;
        CreateTodoCommand command = new CreateTodoCommand("Spring 공부", "JPA 챕터", TodoPriority.HIGH, LocalDate.of(2025, 12, 31));
        Todo saved = createSavedTodo(1L, userId, TodoStatus.PENDING);
        given(todoRepository.save(any())).willReturn(saved);

        TodoResult result = todoService.createTodo(userId, command);

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        verify(todoRepository).save(any());
    }

    @Test
    void getTodos_전체_조회() {
        Long userId = 1L;
        Todo todo1 = createSavedTodo(1L, userId, TodoStatus.PENDING);
        Todo todo2 = createSavedTodo(2L, userId, TodoStatus.IN_PROGRESS);
        given(todoRepository.findByUserId(userId)).willReturn(List.of(todo1, todo2));

        List<TodoResult> results = todoService.getTodos(userId, null);

        assertThat(results).hasSize(2);
        verify(todoRepository).findByUserId(userId);
    }

    @Test
    void getTodos_상태_필터링() {
        Long userId = 1L;
        Todo todo = createSavedTodo(1L, userId, TodoStatus.PENDING);
        given(todoRepository.findByUserIdAndStatus(userId, TodoStatus.PENDING)).willReturn(List.of(todo));

        List<TodoResult> results = todoService.getTodos(userId, TodoStatus.PENDING);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).status()).isEqualTo(TodoStatus.PENDING);
    }

    @Test
    void getTodo_단건_조회() {
        Long todoId = 1L;
        Todo todo = createSavedTodo(todoId, 1L, TodoStatus.PENDING);
        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

        TodoResult result = todoService.getTodo(todoId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(todoId);
    }

    @Test
    void getTodo_존재하지_않으면_예외() {
        Long todoId = 999L;
        given(todoRepository.findById(todoId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.getTodo(todoId))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> org.assertj.core.api.Assertions.assertThat(((CoreException) e).getErrorType()).isEqualTo(ErrorType.NOT_FOUND));
    }

    @Test
    void startTodo_진행중_전환() {
        Long todoId = 1L;
        Todo todo = createSavedTodo(todoId, 1L, TodoStatus.PENDING);
        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(todoRepository.save(any())).willReturn(todo);

        todoService.startTodo(todoId);

        assertThat(todo.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        verify(todoRepository).save(todo);
    }

    @Test
    void completeTodo_완료_처리() {
        Long todoId = 1L;
        Todo todo = createSavedTodo(todoId, 1L, TodoStatus.PENDING);
        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(todoRepository.save(any())).willReturn(todo);

        todoService.completeTodo(todoId);

        assertThat(todo.getStatus()).isEqualTo(TodoStatus.COMPLETED);
        assertThat(todo.getCompletedAt()).isNotNull();
    }

    @Test
    void deleteTodo_할일_삭제() {
        Long todoId = 1L;
        Todo todo = createSavedTodo(todoId, 1L, TodoStatus.PENDING);
        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

        todoService.deleteTodo(todoId);

        verify(todoRepository).deleteById(todoId);
    }

    @Test
    void updateTodo_할일_수정() {
        Long todoId = 1L;
        Todo todo = createSavedTodo(todoId, 1L, TodoStatus.PENDING);
        UpdateTodoCommand command = new UpdateTodoCommand("수정된 제목", "수정된 설명", TodoPriority.HIGH, LocalDate.of(2025, 12, 25));
        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(todoRepository.save(any())).willReturn(todo);

        todoService.updateTodo(todoId, command);

        assertThat(todo.getTitle()).isEqualTo("수정된 제목");
        verify(todoRepository).save(todo);
    }
}
