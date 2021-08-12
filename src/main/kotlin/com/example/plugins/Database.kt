package com.example.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
    fun setup(
        host: String,
        database: String,
        port: String,
        username: String,
        password: String,
    ) {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://$host:$port/$database"
            driverClassName = "org.postgresql.Driver"
            this.username = username
            this.password = password
            maximumPoolSize = 10
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)
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
