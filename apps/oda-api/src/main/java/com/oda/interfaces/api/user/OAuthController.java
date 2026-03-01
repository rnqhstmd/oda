package com.oda.interfaces.api.user;

import com.oda.interfaces.api.ApiResponse;
import com.oda.interfaces.api.user.dto.OAuthLoginRequest;
import com.oda.interfaces.api.user.dto.OAuthLoginResponse;
import com.oda.application.user.OAuthLoginService;
import com.oda.application.user.dto.OAuthLoginCommand;
import com.oda.application.user.dto.OAuthLoginResult;
import com.oda.domain.user.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthLoginService oAuthLoginService;

    @PostMapping("/{provider}")
    public ResponseEntity<ApiResponse<?>> login(
            @PathVariable String provider,
            @RequestBody OAuthLoginRequest request) {

        OAuthProvider oAuthProvider;
        try {
            oAuthProvider = OAuthProvider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("INVALID_PROVIDER", "Unsupported OAuth provider: " + provider));
        }

        OAuthLoginCommand command = new OAuthLoginCommand(oAuthProvider, request.code(), request.redirectUri());
        OAuthLoginResult result = oAuthLoginService.login(command);

        OAuthLoginResponse response = new OAuthLoginResponse(
                result.accessToken(), result.refreshToken(), result.isNewUser());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
