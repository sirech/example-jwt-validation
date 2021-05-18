package com.auth0.jwtValidation.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.math.max

@TestConfiguration
class TestSecurityConfiguration {
    @Autowired
    lateinit var validator: OAuth2TokenValidator<Jwt>

    @Bean
    fun decoder(): JwtDecoder {
        val jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey()).macAlgorithm(MacAlgorithm.HS256).build()
        jwtDecoder.setJwtValidator(validator)
        return jwtDecoder
    }
    private fun secretKey(): SecretKey {
        val keyBytes: ByteArray = "changeme".toByteArray(StandardCharsets.UTF_8)
        val rawKey = ByteArray(max(keyBytes.size, 32))
        System.arraycopy(keyBytes, 0, rawKey, 0, keyBytes.size)
        return SecretKeySpec(rawKey, "HS256")
    }
}