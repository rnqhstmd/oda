package com.oda.infrastructure.notification.persistence;

import com.oda.infrastructure.security.encryption.AesEncryptor;
import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationPreference;
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
@Import({JpaNotificationPreferenceRepository.class, NotificationMapper.class, AesEncryptor.class})
class JpaNotificationPreferenceRepositoryTest {

    @Autowired
    private JpaNotificationPreferenceRepository jpaNotificationPreferenceRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void insertUsers() {
        jdbcTemplate.execute(
            "INSERT INTO users (id, email, name, created_at, updated_at) " +
            "VALUES (1, 'pref1@example.com', 'PrefUser1', NOW(), NOW()) ON CONFLICT DO NOTHING");
        jdbcTemplate.execute(
            "INSERT INTO users (id, email, name, created_at, updated_at) " +
            "VALUES (2, 'pref2@example.com', 'PrefUser2', NOW(), NOW()) ON CONFLICT DO NOTHING");
    }

    @Test
    @DisplayName("알림_설정_저장_및_조회")
    void 알림_설정_저장_및_조회() {
        NotificationPreference pref = NotificationPreference.create(
                1L, NotificationType.DEADLINE_REMINDER, NotificationChannel.IN_APP, true
        );

        NotificationPreference saved = jpaNotificationPreferenceRepository.save(pref);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getType()).isEqualTo(NotificationType.DEADLINE_REMINDER);
        assertThat(saved.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("사용자별_알림_설정_목록_조회")
    void 사용자별_알림_설정_목록_조회() {
        jpaNotificationPreferenceRepository.save(
                NotificationPreference.create(1L, NotificationType.DEADLINE_REMINDER, NotificationChannel.IN_APP, true)
        );
        jpaNotificationPreferenceRepository.save(
                NotificationPreference.create(1L, NotificationType.TODO_REMINDER, NotificationChannel.EMAIL, false)
        );
        jpaNotificationPreferenceRepository.save(
                NotificationPreference.create(2L, NotificationType.POLICY_NEW, NotificationChannel.PUSH, true)
        );

        List<NotificationPreference> user1Prefs = jpaNotificationPreferenceRepository.findByUserId(1L);
        assertThat(user1Prefs).hasSize(2);
    }

    @Test
    @DisplayName("타입_채널로_알림_설정_조회")
    void 타입_채널로_알림_설정_조회() {
        jpaNotificationPreferenceRepository.save(
                NotificationPreference.create(1L, NotificationType.DEADLINE_REMINDER, NotificationChannel.IN_APP, true)
        );

        Optional<NotificationPreference> found = jpaNotificationPreferenceRepository
                .findByUserIdAndTypeAndChannel(1L, NotificationType.DEADLINE_REMINDER, NotificationChannel.IN_APP);

        assertThat(found).isPresent();
        assertThat(found.get().isEnabled()).isTrue();
    }
}
