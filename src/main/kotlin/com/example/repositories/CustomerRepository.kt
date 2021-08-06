package com.example.repositories

import com.example.models.Customer

object CustomerRepository {

    private val store = mutableMapOf<Long, Customer>()

    fun all(): List<Customer> = store.values.toList()

    fun save(customer: Customer): Customer {
        store[customer.id] = customer
        return customer
    }

    fun findById(id: Long): Customer? = store[id]

    fun delete(id: Long) = store.remove(id)

    fun isExists(id: Long) = store.containsKey(id)
}