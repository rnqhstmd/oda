package com.oda.interfaces.api.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oda.infrastructure.security.SecurityConfig;
import com.oda.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.oda.infrastructure.security.jwt.JwtTokenProvider;
import com.oda.application.notification.dto.NotificationResult;
import com.oda.application.notification.NotificationService;
import com.oda.application.notification.NotificationPreferenceService;
import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationPreference;
import com.oda.domain.notification.NotificationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NotificationPreferenceService notificationPreferenceService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UsernamePasswordAuthenticationToken authFor(Long userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    @Test
    void 알림_목록_조회_성공() throws Exception {
        Long userId = 1L;
        NotificationResult result = new NotificationResult(
                1L, "마감 알림", "D-7 마감 예정",
                NotificationType.DEADLINE_REMINDER, false, LocalDateTime.now()
        );
        given(notificationService.getNotifications(anyLong(), anyBoolean()))
                .willReturn(List.of(result));

        mockMvc.perform(get("/api/v1/notifications")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].title").value("마감 알림"));
    }

    @Test
    void 인증_없이_알림_조회_시_401() throws Exception {
        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 미읽음_수_조회_성공() throws Exception {
        Long userId = 1L;
        given(notificationService.getUnreadCount(userId)).willReturn(5L);

        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.count").value(5));
    }

    @Test
    void 알림_읽음_처리_성공() throws Exception {
        mockMvc.perform(patch("/api/v1/notifications/1/read")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));

        verify(notificationService).markAsRead(1L);
    }

    @Test
    void 전체_읽음_처리_성공() throws Exception {
        Long userId = 1L;

        mockMvc.perform(patch("/api/v1/notifications/read-all")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));

        verify(notificationService).markAllAsRead(userId);
    }

    @Test
    void 알림_설정_조회_성공() throws Exception {
        Long userId = 1L;
        NotificationPreference pref = NotificationPreference.create(
                userId, NotificationType.DEADLINE_REMINDER, NotificationChannel.IN_APP, true
        );
        given(notificationPreferenceService.getPreferences(userId)).willReturn(List.of(pref));

        mockMvc.perform(get("/api/v1/notifications/preferences")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].type").value("DEADLINE_REMINDER"));
    }
}
