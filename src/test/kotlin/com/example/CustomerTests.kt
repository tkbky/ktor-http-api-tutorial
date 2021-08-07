package com.example

import com.example.models.Customer
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.repositories.CustomerRepository
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CustomerTests {
    @Before
    fun setup() {
        CustomerRepository.deleteAll()
    }

    @Test
    fun `Get customer returns empty`() {
        withTestApplication({ configureRouting(environment) }) {
            handleRequest(HttpMethod.Get, "/customer").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals("No customers found", response.content)
            }
        }
    }

    @Test
    fun `Get customer returns all customers`() {
        CustomerRepository.save(
            Customer(
                id = 1,
                email = "test@example.com",
                firstName = "test.firstname",
                lastName = "test.lastname"
            )
        )
        withTestApplication({
            configureRouting(environment)
            configureSerialization()
        }) {
            handleRequest(HttpMethod.Get, "/customer").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("""
                    [{"id":1,"firstName":"test.firstname","lastName":"test.lastname","email":"test@example.com"}]
                """.trimIndent(), response.content)
            }
        }
    }

    @Test
    fun `Create customer`() {
        withTestApplication({
            configureRouting(environment)
            configureSerialization()
        }) {
            handleRequest(HttpMethod.Post, "/customer") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("{\"id\":1,\"firstName\":\"test.firstname\",\"lastName\":\"test.lastname\",\"email\":\"test@example.com\"}")
            }
                .apply {
                    assertEquals(HttpStatusCode.Created, response.status())
                    assertEquals("Customer stored correctly", response.content)
                }
        }
    }
}