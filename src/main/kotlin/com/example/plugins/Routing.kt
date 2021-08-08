package com.example.plugins

import com.example.repositories.CustomerRepository
import com.example.routes.registerAuthRoutes
import com.example.routes.registerCustomerRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
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
