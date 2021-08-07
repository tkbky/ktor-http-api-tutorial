package com.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.User
import com.example.plugins.AuthProvider
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

fun Route.authRouting(environment: ApplicationEnvironment) {
    val secret = environment.config.property("ktor.jwt.secret").getString()
    val issuer = environment.config.property("ktor.jwt.issuer").getString()
    val audience = environment.config.property("ktor.jwt.audience").getString()

    route("/auth") {
        authenticate(AuthProvider.AUTH_BASIC.name) {
            get("basic") {
                val principal = call.principal<UserIdPrincipal>()
                principal?.let {
                    call.respondText("Hi ${it.name}!")
                } ?: call.respondText("Hey there!")
            }
        }

        post("jwt-login") {
            val user = call.receive<User>()
            if (user.username == "admin" && user.password == "admin") {
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("username", user.username)
                    .withExpiresAt(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                    .sign(Algorithm.HMAC256(secret))
                call.respond(hashMapOf("token" to token))
            } else {
                call.respondText("Invalid credentials", status = HttpStatusCode.Forbidden)
            }
        }

        authenticate(AuthProvider.AUTH_JWT.name) {
            get("jwt") {
                val principal = call.principal<JWTPrincipal>()
                principal?.let {
                    val username = it.payload.getClaim("username").asString()
                    val expiresAt = it.payload.expiresAt?.time?.minus(System.currentTimeMillis())
                    call.respondText("Hi ${username}, your credentials will expires at $expiresAt")
                } ?: call.respondText("Hey there!")
            }
        }
    }
}

fun Application.registerAuthRoutes() {
    routing {
        authRouting(environment)
    }
}