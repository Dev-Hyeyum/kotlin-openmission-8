package com.example.server.plugins

import com.example.server.RoomController
import com.example.server.models.Component
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Ktor 서버가 실행 중입니다!")
        }

        post("/command") {
            try {
                val command = call.receive<Component>()
                RoomController.broadcastCommand(command)
                call.respond(mapOf("status" to "OK", "received" to command))
            } catch (e: Exception) {
                call.respond(mapOf("status" to "Error", "message" to e.message))
            }
        }

    }
}