package com.oda.interfaces.api.gamification;

import com.oda.interfaces.api.ApiResponse;
import com.oda.interfaces.api.gamification.dto.BadgeResponse;
import com.oda.interfaces.api.gamification.dto.GamificationSummaryResponse;
import com.oda.interfaces.api.gamification.dto.StreakResponse;
import com.oda.application.gamification.BadgeService;
import com.oda.application.gamification.StreakService;
import com.oda.application.gamification.dto.GamificationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gamification")
public class GamificationController {

    private final BadgeService badgeService;
    private final StreakService streakService;

    public GamificationController(BadgeService badgeService,
                                   StreakService streakService) {
        this.badgeService = badgeService;
        this.streakService = streakService;
    }

    @GetMapping("/badges")
    public ResponseEntity<ApiResponse<List<BadgeResponse>>> getBadges(
            @AuthenticationPrincipal Long userId) {
        GamificationResult result = badgeService.getBadges(userId);
        List<BadgeResponse> badges = result.badges().stream()
                .map(b -> new BadgeResponse(b.badge(), b.earnedAt()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(badges));
    }

    @GetMapping("/streak")
    public ResponseEntity<ApiResponse<StreakResponse>> getStreak(
            @AuthenticationPrincipal Long userId) {
        GamificationResult result = badgeService.getBadges(userId);
        return ResponseEntity.ok(ApiResponse.success(
                new StreakResponse(result.currentStreak(), result.longestStreak())
        ));
    }

    @PostMapping("/activity")
    public ResponseEntity<ApiResponse<GamificationSummaryResponse>> recordActivity(
            @AuthenticationPrincipal Long userId) {
        GamificationResult result = streakService.recordActivity(userId);
        List<BadgeResponse> badges = result.badges().stream()
                .map(b -> new BadgeResponse(b.badge(), b.earnedAt()))
                .toList();
        GamificationSummaryResponse response = new GamificationSummaryResponse(
                badges, result.currentStreak(), result.longestStreak()
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
