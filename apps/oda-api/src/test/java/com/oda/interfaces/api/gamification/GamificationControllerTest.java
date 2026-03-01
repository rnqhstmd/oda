package com.oda.interfaces.api.gamification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oda.infrastructure.security.SecurityConfig;
import com.oda.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.oda.infrastructure.security.jwt.JwtTokenProvider;
import com.oda.interfaces.api.gamification.GamificationController;
import com.oda.application.gamification.BadgeService;
import com.oda.application.gamification.StreakService;
import com.oda.application.gamification.dto.BadgeResult;
import com.oda.application.gamification.dto.GamificationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GamificationController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class GamificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BadgeService badgeService;

    @MockBean
    private StreakService streakService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UsernamePasswordAuthenticationToken authFor(Long userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    private GamificationResult sampleResult() {
        return new GamificationResult(
                List.of(new BadgeResult("FIRST_LOGIN", LocalDateTime.now())),
                5,
                10
        );
    }

    @Test
    @DisplayName("배지_목록_조회")
    void 배지_목록_조회() throws Exception {
        given(badgeService.getBadges(anyLong())).willReturn(sampleResult());

        mockMvc.perform(get("/api/v1/gamification/badges")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].badge").value("FIRST_LOGIN"));
    }

    @Test
    @DisplayName("스트릭_조회")
    void 스트릭_조회() throws Exception {
        given(badgeService.getBadges(anyLong())).willReturn(sampleResult());

        mockMvc.perform(get("/api/v1/gamification/streak")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.currentStreak").value(5))
                .andExpect(jsonPath("$.data.longestStreak").value(10));
    }

    @Test
    @DisplayName("활동_기록")
    void 활동_기록() throws Exception {
        given(streakService.recordActivity(anyLong())).willReturn(sampleResult());

        mockMvc.perform(post("/api/v1/gamification/activity")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.currentStreak").value(5));
    }

    @Test
    @DisplayName("인증_없이_배지_조회_401")
    void 인증_없이_배지_조회_401() throws Exception {
        mockMvc.perform(get("/api/v1/gamification/badges"))
                .andExpect(status().isUnauthorized());
    }
}
