package com.oda.application.policy;

import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyDataSource;
import com.oda.domain.policy.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicySyncService {

    private final PolicyDataSource policyDataSource;
    private final PolicyRepository policyRepository;

    @Transactional
    public int syncPolicies() {
        List<Policy> fetched = policyDataSource.fetchPolicies();
        LocalDateTime now = LocalDateTime.now();
        int count = 0;
        for (Policy policy : fetched) {
            policy.markSynced(now);
            policyRepository.save(policy);
            count++;
        }
        return count;
    }
}
