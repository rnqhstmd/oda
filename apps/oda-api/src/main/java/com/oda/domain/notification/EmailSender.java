package com.oda.domain.notification;

public interface EmailSender {
    void sendEmail(String to, String subject, String body);
}
