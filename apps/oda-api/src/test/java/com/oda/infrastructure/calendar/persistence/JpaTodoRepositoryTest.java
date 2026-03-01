package com.oda.infrastructure.calendar.persistence;

import com.oda.domain.calendar.Todo;
import com.oda.domain.calendar.TodoPriority;
import com.oda.domain.calendar.TodoStatus;
import com.oda.infrastructure.security.encryption.AesEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaTodoRepository.class, TodoMapper.class, AesEncryptor.class})
class JpaTodoRepositoryTest {

    @Autowired
    private JpaTodoRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM todos");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("INSERT INTO users (id, email, name, oauth_provider, oauth_id, consent_personal_info, consent_sensitive_info) VALUES (1, 'test1@test.com', 'User1', 'KAKAO', 'kakao1', true, true) ON CONFLICT (id) DO NOTHING");
        jdbcTemplate.execute("INSERT INTO users (id, email, name, oauth_provider, oauth_id, consent_personal_info, consent_sensitive_info) VALUES (2, 'test2@test.com', 'User2', 'KAKAO', 'kakao2', true, true) ON CONFLICT (id) DO NOTHING");
    }

    private Todo createSampleTodo(Long userId, String title, TodoPriority priority) {
        return Todo.create(userId, title, priority, LocalDate.of(2025, 12, 31));
    }

    @Test
    @DisplayName("할일_저장_및_조회")
    void 할일_저장_및_조회() {
        Todo todo = createSampleTodo(1L, "Spring 공부", TodoPriority.HIGH);

        Todo saved = repository.save(todo);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Spring 공부");
        assertThat(saved.getStatus()).isEqualTo(TodoStatus.PENDING);

        Optional<Todo> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Spring 공부");
    }

    @Test
    @DisplayName("사용자별_할일_조회")
    void 사용자별_할일_조회() {
        Long userId = 1L;
        repository.save(createSampleTodo(userId, "할일1", TodoPriority.HIGH));
        repository.save(createSampleTodo(userId, "할일2", TodoPriority.MEDIUM));
        repository.save(createSampleTodo(2L, "다른_사용자_할일", TodoPriority.LOW));

        List<Todo> todos = repository.findByUserId(userId);

        assertThat(todos).hasSize(2);
        assertThat(todos).allMatch(t -> t.getUserId().equals(userId));
    }

    @Test
    @DisplayName("상태별_할일_필터링")
    void 상태별_할일_필터링() {
        Long userId = 1L;
        Todo pending = createSampleTodo(userId, "대기중", TodoPriority.HIGH);
        Todo inProgress = createSampleTodo(userId, "진행중", TodoPriority.MEDIUM);
        inProgress.start();

        repository.save(pending);
        repository.save(inProgress);

        List<Todo> pendingTodos = repository.findByUserIdAndStatus(userId, TodoStatus.PENDING);
        List<Todo> inProgressTodos = repository.findByUserIdAndStatus(userId, TodoStatus.IN_PROGRESS);

        assertThat(pendingTodos).hasSize(1);
        assertThat(pendingTodos.get(0).getStatus()).isEqualTo(TodoStatus.PENDING);
        assertThat(inProgressTodos).hasSize(1);
        assertThat(inProgressTodos.get(0).getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("할일_삭제")
    void 할일_삭제() {
        Todo todo = createSampleTodo(1L, "삭제할 할일", TodoPriority.LOW);
        Todo saved = repository.save(todo);

        repository.deleteById(saved.getId());

        Optional<Todo> found = repository.findById(saved.getId());
        assertThat(found).isEmpty();
    }
}
