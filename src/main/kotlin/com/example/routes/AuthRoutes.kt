package com.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.User
import com.example.plugins.AuthProvider
import io.ktor.application.Application
import io.ktor.application.ApplicationEnvironment
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

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
                    call.respondText("Hi $username, your credentials will expires at $expiresAt")
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
