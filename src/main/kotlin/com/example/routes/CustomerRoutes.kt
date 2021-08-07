package com.example.routes

import com.example.models.Customer
import com.example.plugins.AuthProvider
import com.example.repositories.CustomerRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.customerRouting() {
    route("/customer") {
        authenticate(AuthProvider.AUTH_BASIC.name) {
            get("hi") {
                val principal = call.principal<UserIdPrincipal>()
                principal?.let {
                    call.respondText("Hi ${it.name}!")
                } ?: call.respondText("Hey there!")
            }
        }

        get {
            val customers = CustomerRepository.all()
            if (customers.isEmpty()) {
                call.respondText("No customers found", status = HttpStatusCode.NotFound)
            } else {
                call.respond(CustomerRepository.all())
            }
        }

        get("{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val customer =
                CustomerRepository.findById(id) ?: return@get call.respondText(
                    "No customer with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(customer)
        }

        post {
            val customer = call.receive<Customer>()
            CustomerRepository.save(customer)
            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
        }

        delete("{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (CustomerRepository.isExists(id)) {
                CustomerRepository.delete(id)
                call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}

fun Application.registerCustomerRoutes() {
    routing {
        customerRouting()
    }
}