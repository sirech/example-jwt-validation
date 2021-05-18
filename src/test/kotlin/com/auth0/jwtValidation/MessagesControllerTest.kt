package com.auth0.jwtValidation

import com.auth0.jwtValidation.configuration.TestSecurityConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import(TestSecurityConfiguration::class)
internal class MessagesControllerTest(@Autowired val webApplicationContext: WebApplicationContext) {
    val mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply<DefaultMockMvcBuilder>(springSecurity())
        .build()

    @Test
    fun `returns an accepted answer for the public endpoint`() {
        mockMvc.perform(
            get("/api/messages/public")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.message")
                    .value("The API doesn't require an access token to share this message.")
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
                jsonPath("$.message")
                    .value("The API successfully validated your access token.")
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
                jsonPath("$.message")
                    .value("The API successfully recognized you as an admin.")
            )
    }
}