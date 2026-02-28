package com.oda.domain.notification;

public interface PushSender {
    void sendPush(Long userId, String title, String body);
}
