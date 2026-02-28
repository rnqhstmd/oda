package com.oda.domain.notification;

import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationPreference;
import com.oda.domain.notification.NotificationType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationPreferenceTest {

    @Test
    void create_기본_생성() {
        NotificationPreference pref = NotificationPreference.create(
                1L, NotificationType.DEADLINE_REMINDER, NotificationChannel.IN_APP, true
        );

        assertThat(pref.getUserId()).isEqualTo(1L);
        assertThat(pref.getType()).isEqualTo(NotificationType.DEADLINE_REMINDER);
        assertThat(pref.getChannel()).isEqualTo(NotificationChannel.IN_APP);
        assertThat(pref.isEnabled()).isTrue();
    }

    @Test
    void toggle_활성화_비활성화() {
        NotificationPreference pref = NotificationPreference.create(
                1L, NotificationType.DEADLINE_REMINDER, NotificationChannel.EMAIL, true
        );

        pref.toggle(false);
        assertThat(pref.isEnabled()).isFalse();
    }

    @Test
    void toggle_비활성화_활성화() {
        NotificationPreference pref = NotificationPreference.create(
                1L, NotificationType.TODO_REMINDER, NotificationChannel.PUSH, false
        );

        pref.toggle(true);
        assertThat(pref.isEnabled()).isTrue();
    }
}
