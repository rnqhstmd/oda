package com.oda.application.calendar;

import com.oda.application.calendar.dto.CreateTodoCommand;
import com.oda.application.calendar.dto.TodoResult;
import com.oda.application.calendar.dto.UpdateTodoCommand;
import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.calendar.Todo;
import com.oda.domain.calendar.TodoStatus;
import com.oda.domain.calendar.TodoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoResult createTodo(Long userId, CreateTodoCommand command) {
        Todo todo = Todo.create(userId, command.title(), command.priority(), command.dueDate());
        if (command.description() != null) {
            todo.update(command.title(), command.description(), command.priority(), command.dueDate());
        }
        Todo saved = todoRepository.save(todo);
        return TodoResult.from(saved);
    }

    @Transactional(readOnly = true)
    public List<TodoResult> getTodos(Long userId, TodoStatus filter) {
        List<Todo> todos;
        if (filter != null) {
            todos = todoRepository.findByUserIdAndStatus(userId, filter);
        } else {
            todos = todoRepository.findByUserId(userId);
        }
        return todos.stream().map(TodoResult::from).toList();
    }

    @Transactional(readOnly = true)
    public TodoResult getTodo(Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Todo not found: " + todoId));
        return TodoResult.from(todo);
    }

    public void updateTodo(Long todoId, UpdateTodoCommand command) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Todo not found: " + todoId));
        todo.update(command.title(), command.description(), command.priority(), command.dueDate());
        todoRepository.save(todo);
    }

    public void startTodo(Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Todo not found: " + todoId));
        todo.start();
        todoRepository.save(todo);
    }

    public void completeTodo(Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Todo not found: " + todoId));
        todo.complete();
        todoRepository.save(todo);
    }

    public void deleteTodo(Long todoId) {
        todoRepository.findById(todoId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Todo not found: " + todoId));
        todoRepository.deleteById(todoId);
    }
}
