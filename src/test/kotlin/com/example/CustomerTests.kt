package com.example

import com.example.models.Customer
import com.example.repositories.CustomerRepository
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals

class CustomerTests : KoinTest {
    private val customerRepository: CustomerRepository by inject()

    private fun Application.moduleFunction() {
        (environment.config as MapApplicationConfig).apply {
            put("ktor.jwt.secret", "secret")
            put("ktor.jwt.issuer", "http://0.0.0.0:8080/")
            put("ktor.jwt.audience", "http://0.0.0.0:8080/hello")
            put("ktor.jwt.realm", "Access to 'hello'")
            put("ktor.db.host", "localhost")
            put("ktor.db.port", "5432")
            put("ktor.db.database", "ktor_http_api_tutorial_test")
            put("ktor.db.username", "root")
            put("ktor.db.password", "root")
        }
        module(testing = true)
    }

    @Before
    fun setup() {
        withTestApplication({ moduleFunction() }) {
            transaction {
                val conn = TransactionManager.current().connection
                conn.prepareStatement("TRUNCATE TABLE customers RESTART IDENTITY", returnKeys = false).executeUpdate()
            }
        }
    }

    @Test
    fun `Get customer returns empty`() {
        withTestApplication({ moduleFunction() }) {
            handleRequest(HttpMethod.Get, "/customer").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals("No customers found", response.content)
            }
        }
    }

    @Test
    fun `Get customer returns all customers`() {
        withTestApplication({ moduleFunction() }) {
            customerRepository.save(
                Customer(
                    email = "test@example.com",
                    firstName = "test.firstname",
                    lastName = "test.lastname"
                )
            )
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
        withTestApplication({ moduleFunction() }) {
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