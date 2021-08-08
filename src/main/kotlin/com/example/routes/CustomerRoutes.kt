package com.example.routes

import com.example.models.Customer
import com.example.repositories.CustomerRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.customerRouting(customerRepository: CustomerRepository) {
    route("/customer") {
        get {
            val customers = customerRepository.all()
            if (customers.isEmpty()) {
                call.respondText("No customers found", status = HttpStatusCode.NotFound)
            } else {
                call.respond(customerRepository.all())
            }
        }

        get("{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val customer =
                customerRepository.findById(id) ?: return@get call.respondText(
                    "No customer with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(customer)
        }

        post {
            val customer = call.receive<Customer>()
            customerRepository.save(customer)
            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
        }

        delete("{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (customerRepository.isExists(id)) {
                customerRepository.delete(id)
                call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}

fun Application.registerCustomerRoutes(customerRepository: CustomerRepository) {
    routing {
        customerRouting(customerRepository)
    }
}