package com.example

import com.example.plugins.*
import io.ktor.application.*

//fun main() {
//    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
//        configureSecurity()
//        configureRouting()
//        configureHTTP()
//        configureMonitoring()
//        configureSerialization()
//    }.start(wait = true)
//}
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureDatabase()
    configureKoin()

    configureRouting()
}