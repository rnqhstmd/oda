package com.oda.interfaces.api.user;

import com.oda.interfaces.api.ApiResponse;
import com.oda.interfaces.api.user.dto.ConsentRequest;
import com.oda.interfaces.api.user.dto.ProfileRequest;
import com.oda.interfaces.api.user.dto.ProfileResponse;
import com.oda.interfaces.api.user.dto.UserResponse;
import com.oda.application.user.UserService;
import com.oda.application.user.ProfileService;
import com.oda.application.user.dto.ProfileResult;
import com.oda.application.user.dto.UpdateProfileCommand;
import com.oda.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        User user = userService.getUser(userId);
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(user)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(
            Authentication authentication,
            @RequestBody UserResponse request) {
        Long userId = (Long) authentication.getPrincipal();
        userService.updateName(userId, request.name());
        User user = userService.getUser(userId);
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(user)));
    }

    @GetMapping("/me/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        ProfileResult result = profileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(ProfileResponse.from(result)));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            Authentication authentication,
            @RequestBody ProfileRequest request) {
        Long userId = (Long) authentication.getPrincipal();

        UpdateProfileCommand command = new UpdateProfileCommand(
                request.birthDate(),
                request.sido(),
                request.sigungu(),
                request.employmentStatus(),
                request.educations(),
                request.workExperiences(),
                request.certifications(),
                request.skills(),
                request.targetJobCategories()
        );

        ProfileResult result = profileService.updateProfile(userId, command);
        return ResponseEntity.ok(ApiResponse.success(ProfileResponse.from(result)));
    }

    @PutMapping("/me/consent")
    public ResponseEntity<ApiResponse<Void>> updateConsent(
            Authentication authentication,
            @RequestBody ConsentRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        userService.updateConsent(userId, request.consentPersonalInfo(), request.consentSensitiveInfo());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMe(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
