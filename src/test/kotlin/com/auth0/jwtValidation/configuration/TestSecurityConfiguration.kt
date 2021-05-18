package com.auth0.jwtValidation.configuration

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.*
import java.nio.charset.StandardCharsets
import java.time.Duration
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.math.max

@TestConfiguration
class TestSecurityConfiguration {
    @Bean
    fun decoder(): JwtDecoder {
        val jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey()).macAlgorithm(MacAlgorithm.HS256).build()
        jwtDecoder.setJwtValidator(tokenValidator())
        return jwtDecoder
    }

    private fun tokenValidator(): OAuth2TokenValidator<Jwt> {
        return DelegatingOAuth2TokenValidator(
            JwtTimestampValidator(Duration.ofSeconds(10)),
            JwtIssuerValidator("https://yourTenant.eu.auth0.com/"),
        )
    }

    private fun secretKey(): SecretKey {
        val keyBytes: ByteArray = "changeme".toByteArray(StandardCharsets.UTF_8)
        val rawKey = ByteArray(max(keyBytes.size, 32))
        System.arraycopy(keyBytes, 0, rawKey, 0, keyBytes.size)
        return SecretKeySpec(rawKey, "HS256")
    }
}