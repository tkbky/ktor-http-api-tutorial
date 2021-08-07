package com.example.plugins

import io.ktor.application.*
import io.ktor.auth.*

enum class AuthProvider {
    AUTH_BASIC
}

fun Application.configureSecurity() {
    install(Authentication) {
        basic(AuthProvider.AUTH_BASIC.name) {
            realm = "Access to the '/' path"
            validate { credentials ->
                if (credentials.name == "admin" && credentials.password == "admin") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}
