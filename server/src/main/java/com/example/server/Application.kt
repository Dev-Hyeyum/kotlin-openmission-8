package com.example.server

import com.example.server.plugins.configureRouting
import com.example.server.plugins.configureSerialization
import com.example.server.plugins.configureWebSocket
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 9090, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureWebSocket()
}
