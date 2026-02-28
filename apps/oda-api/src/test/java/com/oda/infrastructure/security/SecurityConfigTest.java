package com.oda.infrastructure.security;

import com.oda.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.oda.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {
    SecurityConfig.class,
    JwtAuthenticationFilter.class,
    SecurityConfigTest.TestConfig.class
})
class SecurityConfigTest {

    private static final String TEST_SECRET = "dGVzdFNlY3JldEtleUZvckp3dFRva2VuUHJvdmlkZXJUZXN0MTIzNDU2Nzg5MA==";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Configuration
    @EnableWebMvc
    @Import(TestSecurityController.class)
    static class TestConfig {
        @Bean
        JwtTokenProvider jwtTokenProvider() {
            return new JwtTokenProvider(TEST_SECRET, 1800000L, 1209600000L);
        }
    }

    @RestController
    static class TestSecurityController {
        @GetMapping("/api/v1/auth/test")
        String publicEndpoint() { return "public"; }

        @GetMapping("/api/v1/users/me")
        String protectedEndpoint() { return "protected"; }
    }

    @Test
    void 공개_경로_인증_불필요() throws Exception {
        mockMvc.perform(get("/api/v1/auth/test"))
            .andExpect(status().isOk());
    }

    @Test
    void 보호_경로_토큰_없으면_401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void 유효_토큰으로_보호_경로_접근() throws Exception {
        String token = jwtTokenProvider.createAccessToken(1L);
        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }
}
