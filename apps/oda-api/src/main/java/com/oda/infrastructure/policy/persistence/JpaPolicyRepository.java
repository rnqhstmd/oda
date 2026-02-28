package com.oda.infrastructure.policy.persistence;

import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyCategory;
import com.oda.domain.policy.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaPolicyRepository implements PolicyRepository {

    private final SpringDataPolicyRepository springDataPolicyRepository;
    private final PolicyMapper policyMapper;

    @Override
    public Policy save(Policy policy) {
        PolicyJpaEntity entity = policyMapper.toEntity(policy);
        PolicyJpaEntity saved = springDataPolicyRepository.save(entity);
        return policyMapper.toDomain(saved);
    }

    @Override
    public Optional<Policy> findById(Long id) {
        return springDataPolicyRepository.findById(id)
                .map(policyMapper::toDomain);
    }

    @Override
    public Page<Policy> findAll(Pageable pageable) {
        return springDataPolicyRepository.findAll(pageable)
                .map(policyMapper::toDomain);
    }

    @Override
    public Page<Policy> findByCategory(PolicyCategory category, Pageable pageable) {
        return springDataPolicyRepository.findByCategory(category, pageable)
                .map(policyMapper::toDomain);
    }

    @Override
    public List<Policy> findByIsActiveTrue() {
        return springDataPolicyRepository.findByActiveTrue().stream()
                .map(policyMapper::toDomain)
                .collect(Collectors.toList());
    }
}
