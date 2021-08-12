package com.example.plugins

import com.example.repositories.CustomerRepository
import com.example.routes.registerAuthRoutes
import com.example.routes.registerCustomerRoutes
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val customerRepository: CustomerRepository by inject()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        registerAuthRoutes()
        registerCustomerRoutes(customerRepository)

        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
