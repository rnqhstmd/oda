package com.oda.application.job.dto;

import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;

import java.time.LocalDate;

public record JobResult(Long id, String title, String companyName, String location,
                        String salary, JobSource source, LocalDate deadline) {

    public static JobResult from(JobPosting job) {
        return new JobResult(
                job.getId(),
                job.getTitle(),
                job.getCompany() != null ? job.getCompany().name() : null,
                job.getLocation(),
                job.getSalary(),
                job.getSource(),
                job.getApplicationDeadline()
        );
    }
}
