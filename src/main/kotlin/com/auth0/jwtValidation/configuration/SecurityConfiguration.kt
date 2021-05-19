package com.auth0.jwtValidation.configuration

import com.auth0.jwtValidation.Message
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.web.cors.CorsConfiguration
import org.springframework.security.web.AuthenticationEntryPoint
import java.io.OutputStream


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfiguration : WebSecurityConfigurerAdapter() {
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
}