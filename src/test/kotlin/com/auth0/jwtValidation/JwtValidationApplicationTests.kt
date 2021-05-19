package com.auth0.jwtValidation

import com.auth0.jwtValidation.configuration.TestSecurityConfiguration
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfiguration::class)
class JwtValidationApplicationTests {

	@Test
	fun contextLoads() {
	}

}
