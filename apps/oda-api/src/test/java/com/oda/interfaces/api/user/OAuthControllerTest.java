package com.oda.interfaces.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oda.interfaces.api.user.dto.OAuthLoginRequest;
import com.oda.application.user.OAuthLoginService;
import com.oda.application.user.dto.OAuthLoginResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.oda.infrastructure.security.SecurityConfig;
import com.oda.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.oda.infrastructure.security.jwt.JwtTokenProvider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OAuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class OAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OAuthLoginService oAuthLoginService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 카카오_소셜로그인_성공() throws Exception {
        // given
        OAuthLoginRequest request = new OAuthLoginRequest("auth-code-123", "http://localhost/callback");
        OAuthLoginResult result = new OAuthLoginResult("access-token", "refresh-token", true, 1L);

        given(oAuthLoginService.login(any())).willReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/auth/oauth/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.isNewUser").value(true));
    }

    @Test
    void 잘못된_Provider_400() throws Exception {
        // given
        OAuthLoginRequest request = new OAuthLoginRequest("auth-code-123", "http://localhost/callback");

        // when & then
        mockMvc.perform(post("/api/v1/auth/oauth/invalid_provider")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.result").value("FAIL"));
    }
}
