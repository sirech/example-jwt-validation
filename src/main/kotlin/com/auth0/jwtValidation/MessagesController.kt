package com.auth0.jwtValidation

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
}