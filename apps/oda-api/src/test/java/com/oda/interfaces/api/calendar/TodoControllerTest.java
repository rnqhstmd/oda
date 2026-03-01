package com.oda.interfaces.api.calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oda.interfaces.api.calendar.dto.CreateTodoRequest;
import com.oda.application.calendar.TodoService;
import com.oda.application.calendar.dto.TodoResult;
import com.oda.domain.calendar.TodoPriority;
import com.oda.domain.calendar.TodoStatus;
import com.oda.infrastructure.security.SecurityConfig;
import com.oda.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.oda.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UsernamePasswordAuthenticationToken authFor(Long userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    private TodoResult sampleTodoResult(Long id) {
        return new TodoResult(
                id, 1L, null, "Spring 공부", "JPA 챕터",
                TodoPriority.HIGH, TodoStatus.PENDING,
                LocalDate.of(2025, 12, 31), null
        );
    }

    @Test
    void 인증_없이_할일_목록_조회_401() throws Exception {
        mockMvc.perform(get("/api/v1/todos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 할일_목록_조회_성공() throws Exception {
        Long userId = 1L;
        given(todoService.getTodos(eq(userId), any())).willReturn(List.of(sampleTodoResult(1L)));

        mockMvc.perform(get("/api/v1/todos")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].title").value("Spring 공부"));
    }

    @Test
    void 할일_생성_성공() throws Exception {
        Long userId = 1L;
        CreateTodoRequest request = new CreateTodoRequest(
                "Spring 공부", "JPA 챕터", TodoPriority.HIGH, LocalDate.of(2025, 12, 31)
        );
        given(todoService.createTodo(eq(userId), any())).willReturn(sampleTodoResult(1L));

        mockMvc.perform(post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.title").value("Spring 공부"));
    }

    @Test
    void 할일_단건_조회_성공() throws Exception {
        Long userId = 1L;
        Long todoId = 1L;
        given(todoService.getTodo(todoId)).willReturn(sampleTodoResult(todoId));

        mockMvc.perform(get("/api/v1/todos/{id}", todoId)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(todoId));
    }

    @Test
    void 할일_진행중_전환_성공() throws Exception {
        Long userId = 1L;
        Long todoId = 1L;

        mockMvc.perform(patch("/api/v1/todos/{id}/start", todoId)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
    }

    @Test
    void 할일_완료_처리_성공() throws Exception {
        Long userId = 1L;
        Long todoId = 1L;

        mockMvc.perform(patch("/api/v1/todos/{id}/complete", todoId)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
    }
}
