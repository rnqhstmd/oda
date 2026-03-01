package com.oda.application.job;

import com.oda.application.job.dto.JobResult;
import com.oda.application.job.dto.JobSearchQuery;
import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetJobsService {

    private final JobPostingRepository jobPostingRepository;

    public Page<JobResult> getJobs(JobSearchQuery query) {
        return jobPostingRepository.findAll(query.pageable())
                .map(JobResult::from);
    }

    public JobPosting getJob(Long id) {
        return jobPostingRepository.findById(id)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Job not found with id: " + id));
    }
}
