package com.oda.infrastructure.policy.persistence;

import com.oda.domain.policy.PolicyCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface SpringDataPolicyRepository extends JpaRepository<PolicyJpaEntity, Long> {
    Page<PolicyJpaEntity> findByCategory(PolicyCategory category, Pageable pageable);
    List<PolicyJpaEntity> findByActiveTrue();
    Page<PolicyJpaEntity> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
