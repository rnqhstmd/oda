package com.oda.application.notification;

import com.oda.domain.calendar.CalendarEvent;
import com.oda.domain.calendar.EventSource;
import com.oda.domain.calendar.EventType;
import com.oda.domain.calendar.CalendarEventRepository;
import com.oda.domain.notification.Notification;
import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationType;
import com.oda.domain.notification.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeadlineReminderServiceTest {

    @Mock
    private CalendarEventRepository calendarEventRepository;

    @Mock
    private NotificationRepository notificationRepository;

    private DeadlineReminderService reminderService;

    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        reminderService = new DeadlineReminderService(
                calendarEventRepository, notificationRepository, new NudgeMessageGenerator()
        );
        // Default: return empty list for all date range queries
        given(calendarEventRepository.findByUserIdAndStartDateBetween(
                eq(USER_ID), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(List.of());
        given(notificationRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
    }

    private CalendarEvent makeEvent(Long id, String title, LocalDate date) {
        return CalendarEvent.reconstruct(
                id, USER_ID, title, null,
                date, date, null, null,
                EventType.POLICY, new EventSource("POLICY", id),
                null, true, null
        );
    }

    @Test
    void D_7_리마인더_생성() {
        LocalDate today = LocalDate.now();
        CalendarEvent event = makeEvent(1L, "청년 취업 지원금", today.plusDays(7));

        given(calendarEventRepository.findByUserIdAndStartDateBetween(
                eq(USER_ID), eq(today.plusDays(7)), eq(today.plusDays(7))))
                .willReturn(List.of(event));

        List<Notification> result = reminderService.checkAndSendRemindersForUser(USER_ID);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getTitle()).contains("D-7");
        assertThat(result.get(0).getType()).isEqualTo(NotificationType.DEADLINE_REMINDER);
        assertThat(result.get(0).getChannel()).isEqualTo(NotificationChannel.IN_APP);
    }

    @Test
    void D_3_리마인더_생성() {
        LocalDate today = LocalDate.now();
        CalendarEvent event = makeEvent(2L, "주거 지원금", today.plusDays(3));

        given(calendarEventRepository.findByUserIdAndStartDateBetween(
                eq(USER_ID), eq(today.plusDays(3)), eq(today.plusDays(3))))
                .willReturn(List.of(event));

        List<Notification> result = reminderService.checkAndSendRemindersForUser(USER_ID);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getTitle()).contains("D-3");
        assertThat(result.get(0).getBody()).contains("필요 서류를 확인하세요");
    }

    @Test
    void D_1_긴급_리마인더() {
        LocalDate today = LocalDate.now();
        CalendarEvent event = makeEvent(3L, "창업 지원금", today.plusDays(1));

        given(calendarEventRepository.findByUserIdAndStartDateBetween(
                eq(USER_ID), eq(today.plusDays(1)), eq(today.plusDays(1))))
                .willReturn(List.of(event));

        List<Notification> result = reminderService.checkAndSendRemindersForUser(USER_ID);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getTitle()).contains("내일 마감!");
        assertThat(result.get(0).getBody()).contains("놓치면 다음 기회는 6개월 뒤입니다");
    }

    @Test
    void D_day_당일_리마인더() {
        LocalDate today = LocalDate.now();
        CalendarEvent event = makeEvent(4L, "교육 지원금", today);

        given(calendarEventRepository.findByUserIdAndStartDateBetween(
                eq(USER_ID), eq(today), eq(today)))
                .willReturn(List.of(event));

        List<Notification> result = reminderService.checkAndSendRemindersForUser(USER_ID);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getTitle()).contains("오늘이 마감일입니다!");
    }

    @Test
    void 만료된_이벤트_알림_없음() {
        List<Notification> result = reminderService.checkAndSendRemindersForUser(USER_ID);

        assertThat(result).isEmpty();
    }
}
