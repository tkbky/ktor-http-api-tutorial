package com.example.plugins

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.util.*

enum class AuthProvider {
    AUTH_BASIC
}

fun Application.configureSecurity() {
    val digestFunction = getDigestFunction("SHA-256") { "ktor-${it.length}" }
    val hashedUserTable = UserHashedTableAuth(
        table = mapOf(
            "admin" to digestFunction("admin")
        ),
        digester = digestFunction
    )
    install(Authentication) {
        basic(AuthProvider.AUTH_BASIC.name) {
            realm = "Access to the '/' path"
            validate { credentials ->
                hashedUserTable.authenticate(credentials)
            }
        }
    }
}
