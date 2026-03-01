package com.oda.interfaces.api.notification;

import com.oda.interfaces.api.ApiResponse;
import com.oda.interfaces.api.notification.dto.NotificationResponse;
import com.oda.interfaces.api.notification.dto.PreferenceResponse;
import com.oda.interfaces.api.notification.dto.UnreadCountResponse;
import com.oda.interfaces.api.notification.dto.UpdatePreferencesRequest;
import com.oda.application.notification.NotificationService;
import com.oda.application.notification.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationPreferenceService notificationPreferenceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        List<NotificationResponse> responses = notificationService.getNotifications(userId, unreadOnly)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(
            @AuthenticationPrincipal Long userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(new UnreadCountResponse(count)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<List<PreferenceResponse>>> getPreferences(
            @AuthenticationPrincipal Long userId) {
        List<PreferenceResponse> responses = notificationPreferenceService.getPreferences(userId)
                .stream()
                .map(PreferenceResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<Void>> updatePreferences(
            @AuthenticationPrincipal Long userId,
            @RequestBody UpdatePreferencesRequest request) {
        notificationPreferenceService.updatePreferences(userId, request.toCommand());
        return ResponseEntity.ok(ApiResponse.success());
    }
}
