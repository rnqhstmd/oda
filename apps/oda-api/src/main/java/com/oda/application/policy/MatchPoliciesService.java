package com.oda.application.policy;

import com.oda.application.policy.dto.PolicyMatchResult;
import com.oda.domain.policy.MatchResult;
import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyMatchingSpec;
import com.oda.domain.policy.PolicyRepository;
import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.user.UserProfile;
import com.oda.domain.user.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchPoliciesService {

    private final PolicyRepository policyRepository;
    private final UserProfileRepository userProfileRepository;

    public List<PolicyMatchResult> matchPolicies(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserProfile not found for userId: " + userId));

        List<Policy> activePolicies = policyRepository.findByIsActiveTrue();

        return activePolicies.stream()
                .map(policy -> {
                    Long personalIncome = profile.getIncomeInfo() != null
                            ? profile.getIncomeInfo().personalIncome() : null;
                    Long householdIncome = profile.getIncomeInfo() != null
                            ? profile.getIncomeInfo().householdIncome() : null;
                    MatchResult result = PolicyMatchingSpec.evaluate(
                            policy,
                            profile.calculateAge(),
                            personalIncome,
                            householdIncome,
                            profile.getSido(),
                            profile.getEmploymentStatus()
                    );
                    return new PolicyMatchResult(policy, result);
                })
                .filter(r -> r.result().eligible())
                .toList();
    }

    public MatchResult checkEligibility(Long policyId, Long userId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Policy not found with id: " + policyId));
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserProfile not found for userId: " + userId));

        Long personalIncome = profile.getIncomeInfo() != null
                ? profile.getIncomeInfo().personalIncome() : null;
        Long householdIncome = profile.getIncomeInfo() != null
                ? profile.getIncomeInfo().householdIncome() : null;

        return PolicyMatchingSpec.evaluate(
                policy,
                profile.calculateAge(),
                personalIncome,
                householdIncome,
                profile.getSido(),
                profile.getEmploymentStatus()
        );
    }
}
