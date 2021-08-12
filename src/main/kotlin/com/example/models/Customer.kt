package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
)