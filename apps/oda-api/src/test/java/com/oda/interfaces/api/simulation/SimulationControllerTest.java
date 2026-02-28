package com.oda.interfaces.api.simulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oda.infrastructure.security.SecurityConfig;
import com.oda.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.oda.infrastructure.security.jwt.JwtTokenProvider;
import com.oda.interfaces.api.simulation.SimulationController;
import com.oda.interfaces.api.simulation.dto.SimulateCareerRequest;
import com.oda.application.simulation.CareerSimulationService;
import com.oda.domain.simulation.CareerPath;
import com.oda.domain.simulation.CareerStep;
import com.oda.domain.simulation.SimulationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SimulationController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class SimulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CareerSimulationService careerSimulationService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UsernamePasswordAuthenticationToken authFor(Long userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    private SimulationResult sampleResult() {
        CareerStep step = new CareerStep("기초 역량 강화", "Spring 학습", 3, List.of("Spring"));
        CareerPath path = new CareerPath(List.of(step), 3, List.of());
        return new SimulationResult("신입", "BACKEND_DEVELOPER", path, List.of("Spring", "Docker"));
    }

    @Test
    @DisplayName("커리어_시뮬레이션_요청")
    void 커리어_시뮬레이션_요청() throws Exception {
        given(careerSimulationService.simulate(anyLong(), anyString())).willReturn(sampleResult());
        SimulateCareerRequest request = new SimulateCareerRequest("BACKEND_DEVELOPER");

        mockMvc.perform(post("/api/v1/simulation/career")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.currentPosition").value("신입"))
                .andExpect(jsonPath("$.data.targetPosition").value("BACKEND_DEVELOPER"))
                .andExpect(jsonPath("$.data.careerPath.steps[0].title").value("기초 역량 강화"))
                .andExpect(jsonPath("$.data.gapItems[0]").exists());
    }

    @Test
    @DisplayName("인증_없이_시뮬레이션_요청_401")
    void 인증_없이_시뮬레이션_요청_401() throws Exception {
        SimulateCareerRequest request = new SimulateCareerRequest("BACKEND_DEVELOPER");

        mockMvc.perform(post("/api/v1/simulation/career")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
