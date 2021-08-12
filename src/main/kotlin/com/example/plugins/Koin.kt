package com.example.plugins

import com.example.repositoryKoinModule
import io.ktor.application.Application
import io.ktor.application.install
import org.koin.ktor.ext.Koin
import org.koin.logger.SLF4JLogger

fun Application.configureKoin() {
    install(Koin) {
        SLF4JLogger()
        modules(repositoryKoinModule)
    }
}
