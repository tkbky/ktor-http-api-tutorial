package com.example.plugins

import io.ktor.application.*
import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
    fun setup(
        host: String,
        database: String,
        port: String,
        username: String,
        password: String,
    ) {
        Database.connect(
            url = "jdbc:postgresql://$host:$port/$database",
            driver = "org.postgresql.Driver",
            user = username,
            password = password
        )
    }
}

fun Application.configureDatabase() {
    DatabaseConfig.setup(
        host = environment.config.property("ktor.db.host").getString(),
        database = environment.config.property("ktor.db.database").getString(),
        port = environment.config.property("ktor.db.port").getString(),
        username = environment.config.property("ktor.db.username").getString(),
        password = environment.config.property("ktor.db.password").getString(),
    )
}