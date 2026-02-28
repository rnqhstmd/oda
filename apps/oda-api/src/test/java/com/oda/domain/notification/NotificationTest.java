package com.oda.domain.notification;

import com.oda.domain.notification.Notification;
import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    @Test
    void create_알림_생성() {
        Notification notification = Notification.create(
                1L, "테스트 제목", "테스트 본문",
                NotificationType.DEADLINE_REMINDER, NotificationChannel.IN_APP,
                10L, "CALENDAR_EVENT"
        );

        assertThat(notification.getUserId()).isEqualTo(1L);
        assertThat(notification.getTitle()).isEqualTo("테스트 제목");
        assertThat(notification.getBody()).isEqualTo("테스트 본문");
        assertThat(notification.getType()).isEqualTo(NotificationType.DEADLINE_REMINDER);
        assertThat(notification.getChannel()).isEqualTo(NotificationChannel.IN_APP);
        assertThat(notification.getReferenceId()).isEqualTo(10L);
        assertThat(notification.getReferenceType()).isEqualTo("CALENDAR_EVENT");
    }

    @Test
    void create_기본값_안읽음() {
        Notification notification = Notification.create(
                1L, "제목", "본문",
                NotificationType.TODO_REMINDER, NotificationChannel.EMAIL,
                null, null
        );

        assertThat(notification.isRead()).isFalse();
    }

    @Test
    void markAsRead_읽음_처리() {
        Notification notification = Notification.create(
                1L, "제목", "본문",
                NotificationType.POLICY_NEW, NotificationChannel.PUSH,
                5L, "POLICY"
        );

        assertThat(notification.isRead()).isFalse();
        notification.markAsRead();
        assertThat(notification.isRead()).isTrue();
    }
}
