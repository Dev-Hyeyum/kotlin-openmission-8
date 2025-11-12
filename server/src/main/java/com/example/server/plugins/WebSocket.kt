package com.example.server.plugins

import com.example.server.RoomController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureWebSocket() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/ws") {
            RoomController.onJoin(this)

            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        println("[WebSocket] 웹에서 메시지 수신 (무시됨): $text")
                    }
                }
            } catch (e: Exception) {
                println("[WebSocket] 오류: ${e.message}")
            } finally {
                RoomController.onLeave(this)
            }
        }
    }
}