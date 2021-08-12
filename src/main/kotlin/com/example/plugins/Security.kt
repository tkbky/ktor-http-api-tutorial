package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserHashedTableAuth
import io.ktor.auth.basic
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.util.getDigestFunction

enum class AuthProvider {
    AUTH_BASIC,
    AUTH_JWT
}

fun Application.configureSecurity() {
    val digestFunction = getDigestFunction("SHA-256") { "ktor-${it.length}" }
    val hashedUserTable = UserHashedTableAuth(
        table = mapOf(
            "admin" to digestFunction("admin")
        ),
        digester = digestFunction
    )
    val secret = environment.config.property("ktor.jwt.secret").getString()
    val issuer = environment.config.property("ktor.jwt.issuer").getString()
    val audience = environment.config.property("ktor.jwt.audience").getString()
    val jwtRealm = environment.config.property("ktor.jwt.realm").getString()
    install(Authentication) {
        basic(AuthProvider.AUTH_BASIC.name) {
            realm = "Access to the '/' path"
            validate { credentials ->
                hashedUserTable.authenticate(credentials)
            }
        }

        jwt(AuthProvider.AUTH_JWT.name) {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { jwtCredential ->
                if (jwtCredential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(jwtCredential.payload)
                } else {
                    null
                }
            }
        }
    }
}
