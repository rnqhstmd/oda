package com.oda.interfaces.scheduler.notification;

import com.oda.application.notification.DeadlineReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeadlineCheckScheduler {

    private final DeadlineReminderService reminderService;

    @Scheduled(cron = "0 0 9 * * *")
    public void checkDeadlines() {
        reminderService.checkAndSendReminders();
    }
}
