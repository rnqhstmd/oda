package com.oda.infrastructure.calendar.persistence;

import com.oda.domain.calendar.CalendarEvent;
import com.oda.domain.calendar.EventType;
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
@Import({JpaCalendarEventRepository.class, CalendarEventMapper.class, AesEncryptor.class})
class JpaCalendarEventRepositoryTest {

    @Autowired
    private JpaCalendarEventRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM calendar_events");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("INSERT INTO users (id, email, name, oauth_provider, oauth_id, consent_personal_info, consent_sensitive_info) VALUES (1, 'test1@test.com', 'User1', 'KAKAO', 'kakao1', true, true) ON CONFLICT (id) DO NOTHING");
        jdbcTemplate.execute("INSERT INTO users (id, email, name, oauth_provider, oauth_id, consent_personal_info, consent_sensitive_info) VALUES (2, 'test2@test.com', 'User2', 'KAKAO', 'kakao2', true, true) ON CONFLICT (id) DO NOTHING");
    }

    private CalendarEvent createSampleEvent(Long userId, LocalDate date) {
        return CalendarEvent.createCustom(
                userId, "테스트 이벤트", "설명",
                date, date, null, null,
                true, null, null
        );
    }

    @Test
    @DisplayName("캘린더_이벤트_저장_및_조회")
    void 캘린더_이벤트_저장_및_조회() {
        CalendarEvent event = createSampleEvent(1L, LocalDate.of(2025, 6, 15));

        CalendarEvent saved = repository.save(event);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("테스트 이벤트");
        assertThat(saved.getType()).isEqualTo(EventType.CUSTOM);

        Optional<CalendarEvent> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("테스트 이벤트");
    }

    @Test
    @DisplayName("사용자_기간별_이벤트_조회")
    void 사용자_기간별_이벤트_조회() {
        Long userId = 1L;
        repository.save(createSampleEvent(userId, LocalDate.of(2025, 6, 10)));
        repository.save(createSampleEvent(userId, LocalDate.of(2025, 6, 20)));
        repository.save(createSampleEvent(userId, LocalDate.of(2025, 7, 5)));
        repository.save(createSampleEvent(2L, LocalDate.of(2025, 6, 15))); // other user

        List<CalendarEvent> events = repository.findByUserIdAndStartDateBetween(
                userId, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30));

        assertThat(events).hasSize(2);
        assertThat(events).allMatch(e -> e.getUserId().equals(userId));
    }

    @Test
    @DisplayName("이벤트_삭제")
    void 이벤트_삭제() {
        CalendarEvent event = createSampleEvent(1L, LocalDate.of(2025, 6, 15));
        CalendarEvent saved = repository.save(event);

        repository.deleteById(saved.getId());

        Optional<CalendarEvent> found = repository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("다른_사용자_이벤트_조회_안됨")
    void 다른_사용자_이벤트_조회_안됨() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        repository.save(createSampleEvent(userId1, LocalDate.of(2025, 6, 15)));
        repository.save(createSampleEvent(userId2, LocalDate.of(2025, 6, 15)));

        List<CalendarEvent> events = repository.findByUserIdAndStartDateBetween(
                userId1, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30));

        assertThat(events).hasSize(1);
        assertThat(events.get(0).getUserId()).isEqualTo(userId1);
    }
}
