package com.oda.infrastructure.job.scheduler;

import com.oda.application.job.JobSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobSyncScheduler {

    private final JobSyncService jobSyncService;

    @Scheduled(cron = "0 0 */12 * * *")
    public void syncJobs() {
        log.info("Starting scheduled job sync");
        jobSyncService.syncJobs();
        log.info("Completed scheduled job sync");
    }
}
