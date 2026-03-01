package com.oda.application.job.dto;

import com.oda.domain.job.GapAnalysis;
import com.oda.domain.job.JobPosting;

public record JobMatchResult(JobPosting job, int matchScore, GapAnalysis gap) {}
