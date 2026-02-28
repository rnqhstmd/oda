package com.oda.interfaces.api.user;

import com.oda.interfaces.api.ApiResponse;
import com.oda.interfaces.api.user.dto.TokenRefreshRequest;
import com.oda.interfaces.api.user.dto.TokenRefreshResponse;
import com.oda.application.user.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(
            @RequestBody TokenRefreshRequest request) {
        String newAccessToken = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(new TokenRefreshResponse(newAccessToken)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
