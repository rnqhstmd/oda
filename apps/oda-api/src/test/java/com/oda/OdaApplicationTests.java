package com.oda;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Requires Docker for Testcontainers PostgreSQL - run manually in CI or local Docker environment")
class OdaApplicationTests {

	@Test
	void contextLoads() {
	}

}
