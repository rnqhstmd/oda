package com.oda.interfaces.scheduler.policy;

import com.oda.application.policy.PolicySyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PolicySyncScheduler {

    private final PolicySyncService policySyncService;

    @Scheduled(cron = "0 0 3 * * *")
    public void syncPolicies() {
        log.info("Starting policy sync...");
        policySyncService.syncPolicies();
        log.info("Policy sync completed.");
    }
}
