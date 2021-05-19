package com.auth0.jwtValidation.configuration

import com.auth0.jwtValidation.Message
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.web.cors.CorsConfiguration
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.web.AuthenticationEntryPoint
import java.io.OutputStream
import java.time.Duration


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfiguration : WebSecurityConfigurerAdapter() {
    @Value("\${auth.audience}")
    lateinit var audience: String

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    lateinit var issuer: String

    override fun configure(http: HttpSecurity) {
        http.let {
            it
                .cors()
                .configurationSource { CorsConfiguration().applyPermitDefaultValues() }
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/messages/public").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer { oauth2 ->
                    oauth2
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .jwt()
                }
        }
    }

    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint {
        return AuthenticationEntryPoint { _, response, authException ->
            response.status = HttpStatus.UNAUTHORIZED.value()
            val out: OutputStream = response.outputStream
            ObjectMapper().writeValue(out, Message(authException.message ?: ""))
            out.flush()
        }
    }

    @Profile("!test")
    @Bean
    fun decoder(): JwtDecoder {
        return (JwtDecoders.fromOidcIssuerLocation(issuer) as NimbusJwtDecoder).also {
            it.setJwtValidator(tokenValidator())
        }
    }

    @Bean
    fun tokenValidator(): OAuth2TokenValidator<Jwt> {
        return DelegatingOAuth2TokenValidator(
            JwtTimestampValidator(Duration.ofSeconds(10)),
            JwtIssuerValidator(issuer),
            audienceValidator()
        )
    }

    private fun audienceValidator(): OAuth2TokenValidator<Jwt> {
        val validator = JwtClaimValidator<List<String>>("aud") { audiences ->
            audience in audiences
        }

        return OAuth2TokenValidator { token ->
            validator.validate(token)
        }
    }
}