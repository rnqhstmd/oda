package com.oda.application.policy;

import com.oda.application.policy.dto.PolicyResult;
import com.oda.application.policy.dto.PolicySearchQuery;
import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPoliciesService {

    private final PolicyRepository policyRepository;

    public Page<PolicyResult> getPolicies(PolicySearchQuery query) {
        Page<Policy> policies;
        if (query.category() != null) {
            policies = policyRepository.findByCategory(query.category(), query.pageable());
        } else {
            policies = policyRepository.findAll(query.pageable());
        }
        return policies.map(PolicyResult::from);
    }

    public PolicyResult getPolicy(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Policy not found with id: " + id));
        return PolicyResult.from(policy);
    }
}
