package com.oda.interfaces.api.calendar;

import com.oda.interfaces.api.calendar.dto.CreateTodoRequest;
import com.oda.interfaces.api.calendar.dto.TodoResponse;
import com.oda.interfaces.api.calendar.dto.UpdateTodoRequest;
import com.oda.application.calendar.TodoService;
import com.oda.application.calendar.dto.TodoResult;
import com.oda.domain.calendar.TodoStatus;
import com.oda.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ApiResponse<List<TodoResponse>> getTodos(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) TodoStatus status) {
        List<TodoResult> results = todoService.getTodos(userId, status);
        return ApiResponse.success(results.stream().map(TodoResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ApiResponse<TodoResponse> getTodo(@PathVariable Long id) {
        TodoResult result = todoService.getTodo(id);
        return ApiResponse.success(TodoResponse.from(result));
    }

    @PostMapping
    public ApiResponse<TodoResponse> createTodo(
            @AuthenticationPrincipal Long userId,
            @RequestBody CreateTodoRequest request) {
        TodoResult result = todoService.createTodo(userId, request.toCommand());
        return ApiResponse.success(TodoResponse.from(result));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateTodo(
            @PathVariable Long id,
            @RequestBody UpdateTodoRequest request) {
        todoService.updateTodo(id, request.toCommand());
        return ApiResponse.success();
    }

    @PatchMapping("/{id}/start")
    public ApiResponse<Void> startTodo(@PathVariable Long id) {
        todoService.startTodo(id);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}/complete")
    public ApiResponse<Void> completeTodo(@PathVariable Long id) {
        todoService.completeTodo(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ApiResponse.success();
    }
}
