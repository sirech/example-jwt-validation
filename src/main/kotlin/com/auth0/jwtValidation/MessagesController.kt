package com.auth0.jwtValidation

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/messages")
class MessagesController {

    @GetMapping("public")
    fun public(): Message {
        return Message("The API doesn't require an access token to share this message.")
    }

    @GetMapping("protected")
    fun protected(): Message {
        return Message("The API successfully validated your access token.")
    }

    @GetMapping("admin")
    @PreAuthorize("hasAuthority('read:admin-messages')")
    fun admin(): Message {
        return Message("The API successfully recognized you as an admin.")
    }
}