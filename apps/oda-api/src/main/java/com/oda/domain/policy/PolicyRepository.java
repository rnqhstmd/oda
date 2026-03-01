package com.oda.domain.policy;

import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository {
    Policy save(Policy policy);
    Optional<Policy> findById(Long id);
    Page<Policy> findAll(Pageable pageable);
    Page<Policy> findByCategory(PolicyCategory category, Pageable pageable);
    List<Policy> findByIsActiveTrue();
}
