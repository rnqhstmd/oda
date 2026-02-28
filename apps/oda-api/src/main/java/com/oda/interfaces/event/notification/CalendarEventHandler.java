package com.oda.interfaces.event.notification;

import com.oda.domain.calendar.event.CalendarEventAddedEvent;
import com.oda.domain.notification.Notification;
import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationType;
import com.oda.domain.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalendarEventHandler {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleCalendarEventAdded(CalendarEventAddedEvent event) {
        Notification notification = Notification.create(
                event.userId(),
                event.title() + " 일정이 추가되었습니다",
                "캘린더에 새 일정이 등록되었습니다.",
                NotificationType.DEADLINE_REMINDER,
                NotificationChannel.IN_APP,
                event.eventId(),
                "CALENDAR_EVENT"
        );
        notificationRepository.save(notification);
    }
}
