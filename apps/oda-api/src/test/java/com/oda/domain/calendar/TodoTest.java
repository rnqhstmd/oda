package com.oda.domain.calendar;

import com.oda.domain.calendar.Todo;
import com.oda.domain.calendar.TodoPriority;
import com.oda.domain.calendar.TodoStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TodoTest {

    @Test
    void create_할일_생성() {
        Todo todo = Todo.create(1L, "Spring 공부", TodoPriority.HIGH, LocalDate.of(2025, 12, 31));

        assertThat(todo.getTitle()).isEqualTo("Spring 공부");
        assertThat(todo.getPriority()).isEqualTo(TodoPriority.HIGH);
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(todo.getUserId()).isEqualTo(1L);
        assertThat(todo.getDueDate()).isEqualTo(LocalDate.of(2025, 12, 31));
    }

    @Test
    void start_진행중_전환() {
        Todo todo = Todo.create(1L, "과제 완료", TodoPriority.MEDIUM, null);

        todo.start();

        assertThat(todo.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
    }

    @Test
    void complete_완료_처리() {
        Todo todo = Todo.create(1L, "이력서 작성", TodoPriority.HIGH, null);
        todo.start();

        todo.complete();

        assertThat(todo.getStatus()).isEqualTo(TodoStatus.COMPLETED);
        assertThat(todo.getCompletedAt()).isNotNull();
    }

    @Test
    void start_이미_진행중_예외() {
        Todo todo = Todo.create(1L, "포트폴리오", TodoPriority.LOW, null);
        todo.start();

        assertThatThrownBy(todo::start)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void complete_완료시_시간_기록() {
        Todo todo = Todo.create(1L, "면접 준비", TodoPriority.HIGH, null);
        todo.complete();

        assertThat(todo.getCompletedAt()).isNotNull();
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.COMPLETED);
    }

    @Test
    void update_할일_수정() {
        Todo todo = Todo.create(1L, "원래 제목", TodoPriority.LOW, null);

        todo.update("수정된 제목", "설명 추가", TodoPriority.HIGH, LocalDate.of(2025, 12, 25));

        assertThat(todo.getTitle()).isEqualTo("수정된 제목");
        assertThat(todo.getDescription()).isEqualTo("설명 추가");
        assertThat(todo.getPriority()).isEqualTo(TodoPriority.HIGH);
        assertThat(todo.getDueDate()).isEqualTo(LocalDate.of(2025, 12, 25));
    }

    @Test
    void create_제목_없으면_예외() {
        assertThatThrownBy(() -> Todo.create(1L, "", TodoPriority.MEDIUM, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("title");
    }

    @Test
    void create_우선순위_없으면_예외() {
        assertThatThrownBy(() -> Todo.create(1L, "제목", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("priority");
    }
}
