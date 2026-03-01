package com.oda.infrastructure.user.persistence;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@TestConfiguration
@EnableJpaAuditing
public class JpaAuditingTestConfig {
}
