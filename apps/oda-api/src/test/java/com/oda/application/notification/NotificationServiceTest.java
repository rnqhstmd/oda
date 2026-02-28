package com.oda.application.notification;

import com.oda.application.notification.dto.NotificationResult;
import com.oda.application.notification.dto.SendNotificationCommand;
import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.notification.Notification;
import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationType;
import com.oda.domain.notification.EmailSender;
import com.oda.domain.notification.NotificationPreferenceRepository;
import com.oda.domain.notification.NotificationRepository;
import com.oda.domain.notification.PushSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Mock
    private EmailSender emailSender;

    @Mock
    private PushSender pushSender;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(
                notificationRepository, notificationPreferenceRepository, emailSender, pushSender
        );
    }

    private Notification makeNotification(Long id, Long userId, boolean read) {
        return Notification.reconstruct(
                id, userId, "테스트 제목", "테스트 본문",
                NotificationType.DEADLINE_REMINDER, NotificationChannel.IN_APP,
                1L, "CALENDAR_EVENT", read
        );
    }

    @Test
    void getNotifications_전체_조회() {
        Long userId = 1L;
        given(notificationRepository.findByUserId(userId))
                .willReturn(List.of(
                        makeNotification(1L, userId, false),
                        makeNotification(2L, userId, true)
                ));

        List<NotificationResult> results = notificationService.getNotifications(userId, false);

        assertThat(results).hasSize(2);
    }

    @Test
    void getNotifications_읽지않은_것만_조회() {
        Long userId = 1L;
        given(notificationRepository.findByUserIdAndReadFalse(userId))
                .willReturn(List.of(makeNotification(1L, userId, false)));

        List<NotificationResult> results = notificationService.getNotifications(userId, true);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).read()).isFalse();
    }

    @Test
    void getUnreadCount_미읽음_수_반환() {
        Long userId = 1L;
        given(notificationRepository.countByUserIdAndReadFalse(userId)).willReturn(3L);

        long count = notificationService.getUnreadCount(userId);

        assertThat(count).isEqualTo(3L);
    }

    @Test
    void markAsRead_읽음_처리_성공() {
        Long notificationId = 1L;
        Notification notification = makeNotification(notificationId, 1L, false);
        given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));
        given(notificationRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        notificationService.markAsRead(notificationId);

        assertThat(notification.isRead()).isTrue();
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_존재하지_않으면_예외() {
        given(notificationRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(99L))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> org.assertj.core.api.Assertions.assertThat(((CoreException) e).getErrorType()).isEqualTo(ErrorType.NOT_FOUND));
    }

    @Test
    void send_인앱_알림_저장() {
        Long userId = 1L;
        SendNotificationCommand command = new SendNotificationCommand(
                userId, "제목", "본문",
                NotificationType.DEADLINE_REMINDER, NotificationChannel.IN_APP,
                1L, "CALENDAR_EVENT"
        );
        given(notificationPreferenceRepository.findByUserIdAndTypeAndChannel(any(), any(), any()))
                .willReturn(Optional.empty());
        given(notificationRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        notificationService.send(command);

        verify(notificationRepository).save(any());
        verify(emailSender, never()).sendEmail(any(), any(), any());
        verify(pushSender, never()).sendPush(any(), any(), any());
    }
}
