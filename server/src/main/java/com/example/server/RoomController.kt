package com.example.server

import com.example.server.models.Component
import com.example.server.models.ComponentAction
import com.example.server.models.ComponentRepository
import com.example.server.models.StateComponent
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

object RoomController {

    private val connections = Collections.synchronizedSet(LinkedHashSet<WebSocketSession>())

    suspend fun onJoin(session: WebSocketSession) {
        connections.add(session)
        println("[WebSocket] 클라이언트 연결: ${session.hashCode()}. (총 ${connections.size}명)")

        val currentState = ComponentRepository.getAll()

        val currentStateCommands = currentState.map {
            Component(
                action = ComponentAction.Create, // 'Create' 명령으로 변환
                id = it.id,
                type = it.type,
                text = it.text
            )
        }
        val initialStateJson = Json.encodeToString(currentStateCommands)
        session.send(Frame.Text(initialStateJson))
    }

    suspend fun broadcastCommand(command: Component) {
        var commandToSend = command

        when (command.action) {
            ComponentAction.Create -> {
                // '명령'을 '상태'로 변환하여 Repository에 저장
                val newId = "${command.type.name.lowercase()}_${System.currentTimeMillis()}"
                val newState = StateComponent(
                    id = newId,
                    type = command.type,
                    text = command.text
                )
                ComponentRepository.addComponent(newState) // Repository에 저장

                commandToSend = command.copy(id = newId)
            }

            ComponentAction.Delete -> {
                command.id.let { ComponentRepository.removeComponent(it) }
            }

            ComponentAction.Update -> {
                command.id.let { componentId ->
                    val existingState = ComponentRepository.getComponent(componentId)
                    if(existingState != null) {
                        val updatedState = existingState.copy(
                            text = command.text // 텍스트만 업데이트
                            // 여기에 위치, 크기 등 다른것도 업데이트하는 로직 추가
                        )
                        ComponentRepository.updateComponent(updatedState)
                    }
                }
            }
        }

        val commandJson = Json.encodeToString(commandToSend)
        connections.forEach { session ->
            session.send(Frame.Text(commandJson))
        }
        println("[Broadcast] 모든 클라이언트에 명령 전송: $commandToSend")
    }

    fun onLeave(session: WebSocketSession) {
        connections.remove(session)
        println("[WebSocket] 클라이언트 연결 끊김: ${session.hashCode()}. (총 ${connections.size}명)")
    }
}