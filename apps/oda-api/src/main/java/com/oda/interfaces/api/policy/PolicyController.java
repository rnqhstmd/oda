package com.oda.interfaces.api.policy;

import com.oda.interfaces.api.ApiResponse;
import com.oda.interfaces.api.policy.dto.EligibilityResponse;
import com.oda.interfaces.api.policy.dto.PolicyDetailResponse;
import com.oda.interfaces.api.policy.dto.PolicyResponse;
import com.oda.application.policy.GetPoliciesService;
import com.oda.application.policy.MatchPoliciesService;
import com.oda.application.policy.dto.PolicyMatchResult;
import com.oda.application.policy.dto.PolicyResult;
import com.oda.application.policy.dto.PolicySearchQuery;
import com.oda.domain.policy.MatchResult;
import com.oda.domain.policy.PolicyCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final GetPoliciesService getPoliciesService;
    private final MatchPoliciesService matchPoliciesService;

    @GetMapping
    public ApiResponse<Page<PolicyResponse>> getPolicies(
            @RequestParam(required = false) PolicyCategory category,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        PolicySearchQuery query = new PolicySearchQuery(category, keyword, pageable);
        Page<PolicyResult> results = getPoliciesService.getPolicies(query);
        return ApiResponse.success(results.map(PolicyResponse::from));
    }

    @GetMapping("/{id}")
    public ApiResponse<PolicyDetailResponse> getPolicy(@PathVariable Long id) {
        PolicyResult result = getPoliciesService.getPolicy(id);
        return ApiResponse.success(PolicyDetailResponse.from(result));
    }

    @GetMapping("/matched")
    public ApiResponse<List<PolicyResponse>> matchPolicies(@AuthenticationPrincipal Long userId) {
        List<PolicyMatchResult> results = matchPoliciesService.matchPolicies(userId);
        List<PolicyResponse> responses = results.stream()
                .map(r -> PolicyResponse.from(PolicyResult.from(r.policy())))
                .toList();
        return ApiResponse.success(responses);
    }

    @GetMapping("/{id}/eligibility")
    public ApiResponse<EligibilityResponse> checkEligibility(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        MatchResult result = matchPoliciesService.checkEligibility(id, userId);
        return ApiResponse.success(new EligibilityResponse(result.eligible(), result.reason()));
    }
}
