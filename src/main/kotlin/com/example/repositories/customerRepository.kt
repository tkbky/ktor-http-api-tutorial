package com.example.repositories

import com.example.models.Customer
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

internal object Customers : LongIdTable() {
    val email: Column<String> = varchar("email", 200).uniqueIndex()
    val firstName: Column<String> = varchar("first_name", 200)
    val lastName: Column<String> = varchar("last_name", 200)

    fun toModel(row: ResultRow): Customer {
        return Customer(
            id = row[Customers.id].value,
            email = row[email],
            firstName = row[firstName],
            lastName = row[lastName]
        )
    }
}

class CustomerRepository {
    init {
        transaction {
            SchemaUtils.create(Customers)
        }
    }

    fun all(): List<Customer> = transaction {
        Customers.selectAll().mapNotNull(Customers::toModel)
    }

    fun save(customer: Customer): Long = transaction {
        Customers.insertAndGetId { row ->
            row[email] = customer.email
            row[firstName] = customer.firstName
            row[lastName] = customer.lastName
        }.value
    }

    fun findById(id: Long): Customer? = transaction {
        Customers.select { Customers.id eq id }.map(Customers::toModel).firstOrNull()
    }

    fun delete(id: Long) = transaction {
        Customers.deleteWhere { Customers.id eq id }
    }

    fun deleteAll() = transaction { Customers.deleteAll() }

    fun isExists(id: Long): Boolean = transaction {
        Customers.slice(exists(Customers.select { Customers.id eq id })).selectAll().none()
    }
}
// object CustomerRepository {
//
//    private val store = mutableMapOf<Long, Customer>()
//
//    fun all(): List<Customer> = store.values.toList()
//
//    fun save(customer: Customer): Customer {
//        store[customer.id] = customer
//        return customer
//    }
//
//    fun findById(id: Long): Customer? = store[id]
//
//    fun delete(id: Long) = store.remove(id)
//
//    fun deleteAll() = store.clear()
//
//    fun isExists(id: Long) = store.containsKey(id)
// }
