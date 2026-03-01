package com.oda.infrastructure.notification.push;

import com.oda.domain.notification.PushSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FcmPushAdapter implements PushSender {

    @Override
    public void sendPush(Long userId, String title, String body) {
        log.info("[FCM STUB] sendPush to userId={}, title={}, body={}", userId, title, body);
        // TODO: Implement FCM integration
    }
}
