package com.oda.infrastructure.job.persistence;

import com.oda.domain.job.JobSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface SpringDataJobPostingRepository extends JpaRepository<JobPostingJpaEntity, Long> {
    List<JobPostingJpaEntity> findBySource(JobSource source);
    List<JobPostingJpaEntity> findByActiveTrue();
    Page<JobPostingJpaEntity> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
