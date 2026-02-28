package com.oda.application.dashboard;

import com.oda.domain.calendar.CalendarEvent;
import com.oda.domain.calendar.EventType;
import com.oda.domain.calendar.Todo;
import com.oda.domain.calendar.TodoPriority;
import com.oda.domain.calendar.TodoStatus;
import com.oda.domain.calendar.CalendarEventRepository;
import com.oda.domain.calendar.TodoRepository;
import com.oda.application.dashboard.dto.DDayItem;
import com.oda.application.dashboard.dto.DashboardResult;
import com.oda.domain.notification.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private CalendarEventRepository calendarEventRepository;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private CalendarEvent makeEvent(Long id, String title, LocalDate startDate, EventType type) {
        return CalendarEvent.reconstruct(
                id, 1L, title, null,
                startDate, startDate,
                null, null,
                type, null, "http://example.com",
                true, null
        );
    }

    private Todo makeTodo(Long id, TodoStatus status) {
        return Todo.reconstruct(id, 1L, null, "할일" + id, null,
                TodoPriority.MEDIUM, status, LocalDate.now(), null);
    }

    @Test
    void 오늘의_할일_요약_정상_반환() {
        Long userId = 1L;
        given(todoRepository.findByUserIdAndStatus(userId, TodoStatus.PENDING))
                .willReturn(List.of(makeTodo(1L, TodoStatus.PENDING), makeTodo(2L, TodoStatus.PENDING)));
        given(todoRepository.findByUserIdAndStatus(userId, TodoStatus.COMPLETED))
                .willReturn(List.of(makeTodo(3L, TodoStatus.COMPLETED)));
        given(calendarEventRepository.findByUserIdAndStartDateBetween(eq(userId), any(), any()))
                .willReturn(Collections.emptyList());
        given(notificationRepository.countByUserIdAndReadFalse(userId)).willReturn(0L);

        DashboardResult result = dashboardService.getDashboard(userId);

        assertThat(result.todaySummary().pendingTodos()).isEqualTo(2L);
        assertThat(result.todaySummary().completedTodos()).isEqualTo(1L);
    }

    @Test
    void D_Day_계산_정확성() {
        Long userId = 1L;
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        given(todoRepository.findByUserIdAndStatus(eq(userId), any())).willReturn(Collections.emptyList());
        given(notificationRepository.countByUserIdAndReadFalse(userId)).willReturn(0L);
        given(calendarEventRepository.findByUserIdAndStartDateBetween(eq(userId), any(), any()))
                .willReturn(List.of(makeEvent(1L, "시험", threeDaysLater, EventType.EXAM)));

        DashboardResult result = dashboardService.getDashboard(userId);

        assertThat(result.dDayItems()).hasSize(1);
        DDayItem item = result.dDayItems().get(0);
        assertThat(item.dDay()).isEqualTo(-3);
        assertThat(item.title()).isEqualTo("시험");
    }

    @Test
    void D_Day_항목_긴급순_정렬() {
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        given(calendarEventRepository.findByUserIdAndStartDateBetween(eq(userId), any(), any()))
                .willReturn(List.of(
                        makeEvent(1L, "먼이벤트", today.plusDays(10), EventType.CUSTOM),
                        makeEvent(2L, "긴급이벤트", today.plusDays(1), EventType.EXAM),
                        makeEvent(3L, "오늘이벤트", today, EventType.JOB)
                ));

        List<DDayItem> items = dashboardService.getDDayItems(userId);

        assertThat(items).hasSize(3);
        assertThat(items.get(0).dDay()).isEqualTo(0);
        assertThat(items.get(1).dDay()).isEqualTo(-1);
        assertThat(items.get(2).dDay()).isEqualTo(-10);
    }

    @Test
    void 빈_데이터_처리() {
        Long userId = 1L;
        given(todoRepository.findByUserIdAndStatus(eq(userId), any())).willReturn(Collections.emptyList());
        given(calendarEventRepository.findByUserIdAndStartDateBetween(eq(userId), any(), any()))
                .willReturn(Collections.emptyList());
        given(notificationRepository.countByUserIdAndReadFalse(userId)).willReturn(0L);

        DashboardResult result = dashboardService.getDashboard(userId);

        assertThat(result.todaySummary().pendingTodos()).isEqualTo(0L);
        assertThat(result.todaySummary().completedTodos()).isEqualTo(0L);
        assertThat(result.todaySummary().upcomingEvents()).isEqualTo(0L);
        assertThat(result.dDayItems()).isEmpty();
        assertThat(result.unreadNotifications()).isEqualTo(0L);
    }

    @Test
    void 미읽은_알림_수_반환() {
        Long userId = 1L;
        given(todoRepository.findByUserIdAndStatus(eq(userId), any())).willReturn(Collections.emptyList());
        given(calendarEventRepository.findByUserIdAndStartDateBetween(eq(userId), any(), any()))
                .willReturn(Collections.emptyList());
        given(notificationRepository.countByUserIdAndReadFalse(userId)).willReturn(5L);

        DashboardResult result = dashboardService.getDashboard(userId);

        assertThat(result.unreadNotifications()).isEqualTo(5L);
    }
}
