package com.example.server.plugins

import com.example.server.RoomController
import com.example.server.models.Component
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // resources/static 폴더를 호스팅하는 설정 추가
        // http://localhost:8080/test.html 로 접속
        staticResources("/", "static") {
            default("test.html")
        }

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