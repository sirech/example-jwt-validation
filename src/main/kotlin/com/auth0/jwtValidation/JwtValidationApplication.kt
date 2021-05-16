package com.auth0.jwtValidation

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JwtValidationApplication

fun main(args: Array<String>) {
	runApplication<JwtValidationApplication>(*args)
}
