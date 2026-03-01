package com.oda.infrastructure.notification.persistence;

import com.oda.infrastructure.security.encryption.AesEncryptor;
import com.oda.domain.notification.Notification;
import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaNotificationRepository.class, NotificationMapper.class, AesEncryptor.class})
class JpaNotificationRepositoryTest {

    @Autowired
    private JpaNotificationRepository jpaNotificationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void insertUsers() {
        jdbcTemplate.execute(
            "INSERT INTO users (id, email, name, created_at, updated_at) " +
            "VALUES (1, 'test1@example.com', 'User1', NOW(), NOW()) ON CONFLICT DO NOTHING");
        jdbcTemplate.execute(
            "INSERT INTO users (id, email, name, created_at, updated_at) " +
            "VALUES (2, 'test2@example.com', 'User2', NOW(), NOW()) ON CONFLICT DO NOTHING");
    }

    private Notification createNotification(Long userId, boolean read) {
        Notification n = Notification.create(
                userId, "테스트 알림", "알림 본문",
                NotificationType.DEADLINE_REMINDER, NotificationChannel.IN_APP,
                1L, "CALENDAR_EVENT"
        );
        if (read) n.markAsRead();
        return n;
    }

    @Test
    @DisplayName("알림_저장_및_조회")
    void 알림_저장_및_조회() {
        Notification saved = jpaNotificationRepository.save(createNotification(1L, false));

        assertThat(saved.getId()).isNotNull();
        Optional<Notification> found = jpaNotificationRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("테스트 알림");
    }

    @Test
    @DisplayName("사용자별_알림_조회")
    void 사용자별_알림_조회() {
        jpaNotificationRepository.save(createNotification(1L, false));
        jpaNotificationRepository.save(createNotification(1L, true));
        jpaNotificationRepository.save(createNotification(2L, false));

        List<Notification> user1Notifications = jpaNotificationRepository.findByUserId(1L);
        assertThat(user1Notifications).hasSize(2);
    }

    @Test
    @DisplayName("미읽음_알림_조회")
    void 미읽음_알림_조회() {
        jpaNotificationRepository.save(createNotification(1L, false));
        jpaNotificationRepository.save(createNotification(1L, false));
        jpaNotificationRepository.save(createNotification(1L, true));

        List<Notification> unread = jpaNotificationRepository.findByUserIdAndReadFalse(1L);
        assertThat(unread).hasSize(2);
        assertThat(unread).allMatch(n -> !n.isRead());
    }

    @Test
    @DisplayName("미읽음_수_카운트")
    void 미읽음_수_카운트() {
        jpaNotificationRepository.save(createNotification(1L, false));
        jpaNotificationRepository.save(createNotification(1L, false));
        jpaNotificationRepository.save(createNotification(1L, true));

        long count = jpaNotificationRepository.countByUserIdAndReadFalse(1L);
        assertThat(count).isEqualTo(2L);
    }
}
