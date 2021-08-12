package com.example

import com.example.plugins.configureDatabase
import com.example.plugins.configureHTTP
import com.example.plugins.configureKoin
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import io.ktor.application.Application

// fun main() {
//    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
//        configureSecurity()
//        configureRouting()
//        configureHTTP()
//        configureMonitoring()
//        configureSerialization()
//    }.start(wait = true)
// }
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureDatabase()
    configureKoin()

    configureRouting()
}
