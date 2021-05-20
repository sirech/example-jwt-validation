package com.auth0.jwtValidation

import com.auth0.jwtValidation.configuration.TestSecurityConfiguration
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestSecurityConfiguration::class)
internal class MessagesControllerTest(@Autowired val webApplicationContext: WebApplicationContext) {
    val mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply<DefaultMockMvcBuilder>(springSecurity())
        .build()

    @Test
    fun `returns a 404 por routes that don't exist`() {
        mockMvc.perform(
            get("/api/test")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `returns an accepted answer for the public endpoint`() {
        mockMvc.perform(
            get("/api/messages/public")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath(
                    "$.message",
                    `is`("The API doesn't require an access token to share this message.")
                )
            )
    }

    @Test
    fun `returns an accepted answer for the protected endpoint`() {
        val token = "validToken".asStream().readTextAndClose()
        mockMvc.perform(
            get("/api/messages/protected")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath(
                    "$.message", `is`("The API successfully validated your access token.")
                )
            )
    }

    @Test
    fun `returns error for the protected endpoint if there is no token`() {
        mockMvc.perform(
            get("/api/messages/protected")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isUnauthorized)
            .andExpect(
                jsonPath("$.message", `is`("Full authentication is required to access this resource"))
            )
    }

    @Test
    fun `returns error for the protected endpoint if the token is expired`() {
        val token = "expiredToken".asStream().readTextAndClose()
        mockMvc.perform(
            get("/api/messages/protected")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isUnauthorized)
            .andExpect(
                jsonPath("$.message", containsString("Jwt expired"))
            )
    }

    @Test
    fun `returns error for the protected endpoint if the token has the wrong issuer`() {
        val token = "wrongIssuerToken".asStream().readTextAndClose()
        mockMvc.perform(
            get("/api/messages/protected")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isUnauthorized)
            .andExpect(
                jsonPath("$.message", containsString("The iss claim is not valid"))
            )
    }

    @Test
    fun `returns error for the protected endpoint if the token has the wrong audience`() {
        val token = "wrongAudienceToken".asStream().readTextAndClose()
        mockMvc.perform(
            get("/api/messages/protected")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isUnauthorized)
            .andExpect(
                jsonPath("$.message", containsString("The aud claim is not valid"))
            )
    }

    @Test
    fun `returns error for the admin endpoint if the token doesn't have permissions`() {
        val token = "validToken".asStream().readTextAndClose()
        mockMvc.perform(
            get("/api/messages/admin")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isUnauthorized)
            .andExpect(
                jsonPath("$.message", containsString("Access is denied"))
            )
    }

    @Test
    fun `returns an accepted answer for the admin endpoint`() {
        val token = "validWithPermissionsToken".asStream().readTextAndClose()
        mockMvc.perform(
            get("/api/messages/admin")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath(
                    "$.message",
                    `is`("The API successfully recognized you as an admin.")
                )
            )
    }

}