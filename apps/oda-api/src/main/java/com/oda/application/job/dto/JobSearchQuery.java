package com.oda.application.job.dto;

import com.oda.domain.job.JobSource;
import org.springframework.data.domain.Pageable;

public record JobSearchQuery(String keyword, String location, JobSource source, Pageable pageable) {}
