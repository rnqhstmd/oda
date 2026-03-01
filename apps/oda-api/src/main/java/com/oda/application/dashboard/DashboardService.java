package com.oda.application.dashboard;

import com.oda.domain.calendar.CalendarEvent;
import com.oda.domain.calendar.TodoStatus;
import com.oda.domain.calendar.CalendarEventRepository;
import com.oda.domain.calendar.TodoRepository;
import com.oda.application.dashboard.dto.DDayItem;
import com.oda.application.dashboard.dto.DashboardResult;
import com.oda.application.dashboard.dto.TodaySummary;
import com.oda.domain.notification.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final CalendarEventRepository calendarEventRepository;
    private final TodoRepository todoRepository;
    private final NotificationRepository notificationRepository;

    public DashboardService(CalendarEventRepository calendarEventRepository,
                            TodoRepository todoRepository,
                            NotificationRepository notificationRepository) {
        this.calendarEventRepository = calendarEventRepository;
        this.todoRepository = todoRepository;
        this.notificationRepository = notificationRepository;
    }

    public DashboardResult getDashboard(Long userId) {
        long pendingTodos = todoRepository.findByUserIdAndStatus(userId, TodoStatus.PENDING).size();
        long completedTodos = todoRepository.findByUserIdAndStatus(userId, TodoStatus.COMPLETED).size();

        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysLater = today.plusDays(30);
        long upcomingEvents = calendarEventRepository
                .findByUserIdAndStartDateBetween(userId, today, thirtyDaysLater).size();

        long unreadNotifications = notificationRepository.countByUserIdAndReadFalse(userId);

        List<DDayItem> dDayItems = getDDayItems(userId);

        TodaySummary todaySummary = new TodaySummary(pendingTodos, completedTodos, upcomingEvents);
        return new DashboardResult(todaySummary, dDayItems, unreadNotifications);
    }

    public List<DDayItem> getDDayItems(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysLater = today.plusDays(30);

        List<CalendarEvent> events = calendarEventRepository
                .findByUserIdAndStartDateBetween(userId, today, thirtyDaysLater);

        return events.stream()
                .map(event -> {
                    long daysUntil = ChronoUnit.DAYS.between(today, event.getStartDate());
                    int dDay = (int) (daysUntil * -1);
                    return new DDayItem(
                            event.getId(),
                            dDay,
                            event.getTitle(),
                            event.getType() != null ? event.getType().name() : null,
                            event.getActionUrl()
                    );
                })
                .sorted(Comparator.comparingInt(item -> Math.abs(item.dDay())))
                .collect(Collectors.toList());
    }
}
