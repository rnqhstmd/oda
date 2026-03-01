package com.oda.application.notification;

import com.oda.application.notification.dto.NotificationResult;
import com.oda.application.notification.dto.SendNotificationCommand;
import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.notification.Notification;
import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.EmailSender;
import com.oda.domain.notification.NotificationPreferenceRepository;
import com.oda.domain.notification.NotificationRepository;
import com.oda.domain.notification.PushSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final EmailSender emailSender;
    private final PushSender pushSender;

    @Transactional(readOnly = true)
    public List<NotificationResult> getNotifications(Long userId, boolean unreadOnly) {
        List<Notification> notifications = unreadOnly
                ? notificationRepository.findByUserIdAndReadFalse(userId)
                : notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(this::toResult)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Notification not found: " + notificationId));
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(userId);
        unread.forEach(Notification::markAsRead);
        unread.forEach(notificationRepository::save);
    }

    public void send(SendNotificationCommand command) {
        boolean preferred = notificationPreferenceRepository
                .findByUserIdAndTypeAndChannel(command.userId(), command.type(), command.channel())
                .map(p -> p.isEnabled())
                .orElse(true);

        if (!preferred) {
            return;
        }

        Notification notification = Notification.create(
                command.userId(),
                command.title(),
                command.body(),
                command.type(),
                command.channel(),
                command.referenceId(),
                command.referenceType()
        );
        notificationRepository.save(notification);

        if (command.channel() == NotificationChannel.EMAIL) {
            emailSender.sendEmail(command.userId().toString(), command.title(), command.body());
        } else if (command.channel() == NotificationChannel.PUSH) {
            pushSender.sendPush(command.userId(), command.title(), command.body());
        }
    }

    private NotificationResult toResult(Notification n) {
        return new NotificationResult(
                n.getId(),
                n.getTitle(),
                n.getBody(),
                n.getType(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
