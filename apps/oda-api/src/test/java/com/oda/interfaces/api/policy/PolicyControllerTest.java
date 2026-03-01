package com.oda.interfaces.api.policy;

import com.oda.infrastructure.security.SecurityConfig;
import com.oda.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.oda.infrastructure.security.jwt.JwtTokenProvider;
import com.oda.application.policy.GetPoliciesService;
import com.oda.application.policy.MatchPoliciesService;
import com.oda.application.policy.dto.PolicyMatchResult;
import com.oda.application.policy.dto.PolicyResult;
import com.oda.application.policy.dto.PolicySearchQuery;
import com.oda.domain.policy.MatchResult;
import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PolicyController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetPoliciesService getPoliciesService;

    @MockBean
    private MatchPoliciesService matchPoliciesService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UsernamePasswordAuthenticationToken authFor(Long userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    private PolicyResult samplePolicyResult(Long id) {
        return new PolicyResult(
                id,
                "청년 취업 지원금",
                "청년 취업 지원 정책",
                PolicyCategory.EMPLOYMENT,
                "고용노동부",
                LocalDate.of(2025, 12, 31),
                "http://example.com",
                true
        );
    }

    @Test
    void 정책_목록_조회_성공() throws Exception {
        PolicyResult result = samplePolicyResult(1L);
        given(getPoliciesService.getPolicies(any(PolicySearchQuery.class)))
                .willReturn(new PageImpl<>(List.of(result)));

        mockMvc.perform(get("/api/v1/policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].title").value("청년 취업 지원금"));
    }

    @Test
    void 정책_단건_조회_성공() throws Exception {
        Long userId = 1L;
        PolicyResult result = samplePolicyResult(1L);
        given(getPoliciesService.getPolicy(1L)).willReturn(result);

        mockMvc.perform(get("/api/v1/policies/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("청년 취업 지원금"));
    }

    @Test
    void 인증_없이_매칭_요청시_401() throws Exception {
        mockMvc.perform(get("/api/v1/policies/matched"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 매칭_정책_조회_성공() throws Exception {
        Long userId = 1L;
        Policy policy = Policy.create("청년 취업 지원금", PolicyCategory.EMPLOYMENT, null);
        PolicyMatchResult matchResult = new PolicyMatchResult(policy, MatchResult.ofEligible());

        given(matchPoliciesService.matchPolicies(userId)).willReturn(List.of(matchResult));

        mockMvc.perform(get("/api/v1/policies/matched")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
    }

    @Test
    void 자격_검사_성공() throws Exception {
        Long userId = 1L;
        Long policyId = 1L;
        given(matchPoliciesService.checkEligibility(policyId, userId))
                .willReturn(MatchResult.ofEligible());

        mockMvc.perform(get("/api/v1/policies/{id}/eligibility", policyId)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.eligible").value(true));
    }

    @Test
    void 자격_없음_응답() throws Exception {
        Long userId = 1L;
        Long policyId = 2L;
        given(matchPoliciesService.checkEligibility(policyId, userId))
                .willReturn(MatchResult.ofIneligible("나이 초과"));

        mockMvc.perform(get("/api/v1/policies/{id}/eligibility", policyId)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.eligible").value(false))
                .andExpect(jsonPath("$.data.reason").value("나이 초과"));
    }
}
