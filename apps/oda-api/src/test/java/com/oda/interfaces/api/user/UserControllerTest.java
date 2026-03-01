package com.oda.interfaces.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oda.infrastructure.security.SecurityConfig;
import com.oda.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.oda.infrastructure.security.jwt.JwtTokenProvider;
import com.oda.application.user.UserService;
import com.oda.application.user.ProfileService;
import com.oda.application.user.dto.ProfileResult;
import com.oda.domain.user.OAuthProvider;
import com.oda.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UsernamePasswordAuthenticationToken authFor(Long userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    @Test
    void 인증_없이_접근시_401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 내_정보_조회_성공() throws Exception {
        // given
        Long userId = 1L;
        User user = User.createFromOAuth(OAuthProvider.KAKAO, "kakao-123", "test@example.com", "테스트유저");
        ReflectionTestUtils.setField(user, "id", userId);

        given(userService.getUser(userId)).willReturn(user);

        // when & then
        mockMvc.perform(get("/api/v1/users/me")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.name").value("테스트유저"));
    }

    @Test
    void 프로필_조회_성공() throws Exception {
        // given
        Long userId = 1L;
        ProfileResult profileResult = new ProfileResult(10L, userId, null, null,
                "서울", "강남구", null, null, List.of(), List.of(), List.of(), List.of(), List.of());

        given(profileService.getProfile(userId)).willReturn(profileResult);

        // when & then
        mockMvc.perform(get("/api/v1/users/me/profile")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.sido").value("서울"))
                .andExpect(jsonPath("$.data.sigungu").value("강남구"));
    }

    @Test
    void 동의_업데이트_성공() throws Exception {
        // given
        Long userId = 1L;
        String body = "{\"consentPersonalInfo\":true,\"consentSensitiveInfo\":false}";

        // when & then
        mockMvc.perform(put("/api/v1/users/me/consent")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
    }
}
