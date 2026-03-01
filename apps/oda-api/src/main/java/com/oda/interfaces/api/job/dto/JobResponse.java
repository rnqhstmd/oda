package com.oda.interfaces.api.job.dto;

import com.oda.application.job.dto.JobResult;
import com.oda.domain.job.JobSource;

import java.time.LocalDate;

public record JobResponse(Long id, String title, String companyName, String location,
                          String salary, JobSource source, LocalDate deadline) {

    public static JobResponse from(JobResult result) {
        return new JobResponse(
                result.id(),
                result.title(),
                result.companyName(),
                result.location(),
                result.salary(),
                result.source(),
                result.deadline()
        );
    }
}
