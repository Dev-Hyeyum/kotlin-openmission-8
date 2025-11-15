// server/src/main/java/com/example/server/Room.kt

package com.example.server

import com.example.server.models.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

// object 싱글톤에서 일반 클래스로 변환
class Room {
    // 이 'Room' 만의 '개인' 변수들
    private val connections = Collections.synchronizedSet(LinkedHashSet<WebSocketSession>())

    // 컴포넌트를 저장하는 Room의 저장소
    private val repository = ComponentRepository()
    private val json = Json { encodeDefaults = true }

    suspend fun onJoin(session: WebSocketSession) {
        connections.add(session)
        println("[WebSocket] 클라이언트 연결: ${session.hashCode()}. (총 ${connections.size}명)")

        // 'repository'에서 List<Component>를 바로 가져옴
        val currentStateCommands = repository.getAll()

        val initialStateJson = json.encodeToString(currentStateCommands)
        session.send(Frame.Text(initialStateJson))
    }

    suspend fun broadcastCommand(command: Component) {
        val commandToSend = command

        when (command.action) {
            ComponentAction.Create -> {
                // 'command' 객체를 'repository'에 그대로 저장
                repository.addComponent(command)
            }

            ComponentAction.Delete -> {
                repository.removeComponent(command.id)
            }

            ComponentAction.Update -> {
                // 'command' 객체로 'repository'를 업데이트
                repository.updateComponent(command)
            }
        }

        val commandJson = json.encodeToString(commandToSend)
        connections.forEach { session ->
            try {
                session.send(Frame.Text(commandJson))
            } catch (e: Exception) {
                println("[Broadcast 실패] ${e.message}")
            }
        }
        println("[Broadcast] '${this.hashCode()}' 방에 명령 전송: ${commandToSend.action}")
    }
    fun onLeave(session: WebSocketSession) {
        connections.remove(session)
        println("[WebSocket] 클라이언트 연결 끊김: ${session.hashCode()}. (총 ${connections.size}명)")
    }
}