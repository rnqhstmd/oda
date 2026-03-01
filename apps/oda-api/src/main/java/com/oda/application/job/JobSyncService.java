package com.oda.application.job;

import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobDataSource;
import com.oda.domain.job.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobSyncService {

    private final JobPostingRepository jobPostingRepository;
    private final List<JobDataSource> jobDataSources;

    @Transactional
    public void syncJobs() {
        for (JobDataSource dataSource : jobDataSources) {
            try {
                List<JobPosting> jobs = dataSource.fetchJobs();
                for (JobPosting job : jobs) {
                    jobPostingRepository.save(job);
                }
                log.info("Synced {} jobs from data source", jobs.size());
            } catch (Exception e) {
                log.error("Failed to sync jobs from data source: {}", e.getMessage(), e);
            }
        }
    }
}
