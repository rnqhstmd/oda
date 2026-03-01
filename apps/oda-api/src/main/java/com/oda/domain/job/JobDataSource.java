package com.oda.domain.job;

import com.oda.domain.job.JobPosting;

import java.util.List;

public interface JobDataSource {
    List<JobPosting> fetchJobs();
}
