package com.oda.domain.job;

import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface JobPostingRepository {
    JobPosting save(JobPosting jobPosting);
    Optional<JobPosting> findById(Long id);
    Page<JobPosting> findAll(Pageable pageable);
    List<JobPosting> findBySource(JobSource source);
    List<JobPosting> findByActiveTrue();
}
