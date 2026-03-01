package com.oda.application.notification;

import com.oda.domain.calendar.CalendarEvent;
import com.oda.domain.calendar.CalendarEventRepository;
import com.oda.domain.notification.Notification;
import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationType;
import com.oda.domain.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeadlineReminderService {

    private static final List<Integer> REMINDER_DAYS = List.of(7, 3, 1, 0);

    private final CalendarEventRepository calendarEventRepository;
    private final NotificationRepository notificationRepository;
    private final NudgeMessageGenerator nudgeGenerator;

    public void checkAndSendReminders() {
        LocalDate today = LocalDate.now();

        for (int daysUntil : REMINDER_DAYS) {
            LocalDate targetDate = today.plusDays(daysUntil);
            List<CalendarEvent> events = calendarEventRepository
                    .findByUserIdAndStartDateBetween(null, targetDate, targetDate);
            processEvents(events, daysUntil);
        }
    }

    public List<Notification> checkAndSendRemindersForUser(Long userId) {
        LocalDate today = LocalDate.now();
        List<Notification> created = new java.util.ArrayList<>();

        for (int daysUntil : REMINDER_DAYS) {
            LocalDate targetDate = today.plusDays(daysUntil);
            List<CalendarEvent> events = calendarEventRepository
                    .findByUserIdAndStartDateBetween(userId, targetDate, targetDate);

            for (CalendarEvent event : events) {
                String title = NudgeMessageGenerator.generateTitle(event.getTitle(), daysUntil);
                String body = NudgeMessageGenerator.generateBody(event.getTitle(), daysUntil, event.getActionUrl());

                Notification notification = Notification.create(
                        event.getUserId(),
                        title,
                        body,
                        NotificationType.DEADLINE_REMINDER,
                        NotificationChannel.IN_APP,
                        event.getId(),
                        "CALENDAR_EVENT"
                );
                created.add(notificationRepository.save(notification));
            }
        }
        return created;
    }

    private void processEvents(List<CalendarEvent> events, int daysUntil) {
        for (CalendarEvent event : events) {
            String title = NudgeMessageGenerator.generateTitle(event.getTitle(), daysUntil);
            String body = NudgeMessageGenerator.generateBody(event.getTitle(), daysUntil, event.getActionUrl());

            Notification notification = Notification.create(
                    event.getUserId(),
                    title,
                    body,
                    NotificationType.DEADLINE_REMINDER,
                    NotificationChannel.IN_APP,
                    event.getId(),
                    "CALENDAR_EVENT"
            );
            notificationRepository.save(notification);
        }
    }
}
