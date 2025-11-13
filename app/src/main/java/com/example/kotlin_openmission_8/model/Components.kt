package com.example.kotlin_openmission_8.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class Components(private val client: HttpClient): ViewModel() {
    private val _components = MutableStateFlow<List<Component>>(emptyList())
    val components: StateFlow<List<Component>> = _components.asStateFlow()

    private val _canvasScrollState = MutableStateFlow(Pair(0f, 0f))
    val canvasScrollState: StateFlow<Pair<Float, Float>> = _canvasScrollState.asStateFlow()

    private val BASE_URL = "http://10.0.2.2:8080"
    private val WS_URL = "ws://10.0.2.2:8080/ws" // ⚡ WebSocket 경로

    private var isConnected = false

    private suspend fun sendCommand(component: Component, logTag: String) {
        try {
            val response = client.post("$BASE_URL/command") {
                contentType(ContentType.Application.Json)
                setBody(component)
            }
            println("✅ $logTag 성공: ${response.status} (ID: ${component.id})")
        } catch (e: Exception) {
            println("❌ $logTag 실패: ${e.message}")
        }
    }

    fun postComponent(component: Component) {
        viewModelScope.launch {
            sendCommand(component, "생성")
        }
    }

    fun deleteComponent(id: String) {
        viewModelScope.launch {
            val deleteCommand = Component(
                action = ComponentAction.Delete,
                type = ComponentType.Text,
                text = "",
                id = id
            )

            sendCommand(deleteCommand, "삭제")
        }
    }

    fun updateComponent(
        id: String,
        offsetX: Float? = null,
        offsetY: Float? = null,
        width: Float? = null,
        height: Float? = null,
        text: String? = null
    ) {
        viewModelScope.launch {
            _components.update { current ->
                current.map { component ->
                    if (component.id == id) {
                        component.copy(
                            action = ComponentAction.Update,
                            text = text ?: component.text,
                            width = width ?: component.width,
                            height = height ?: component.height,
                            offsetX = offsetX ?: component.offsetX,
                            offsetY = offsetY ?: component.offsetY
                        )
                    } else {
                        component
                    }
                }
            }

            val updatedComponent = _components.value.first { it.id == id }
            val updateCommand = updatedComponent.copy(action = ComponentAction.Update)

            sendCommand(updateCommand, "수정")
        }
    }

    fun connectWebSocket() {
        if(isConnected) return
        isConnected = true

        viewModelScope.launch {
            try {
                client.webSocket(WS_URL) {
                    println("✅ WebSocket 연결 성공")
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val message = frame.readText()
                            try {
                                if (message.trim().startsWith("[")) {
                                    // 1. 초기 전체 리스트 수신
                                    val allComponents = Json.decodeFromString<List<Component>>(message)
                                    _components.value = allComponents
                                    println("📦 초기 데이터 로드 완료: ${allComponents.size}개")
                                } else {
                                    // 2. 단일 명령 수신
                                    val command = Json.decodeFromString<Component>(message)
                                    handleCommand(command)
                                }
                            } catch (e: Exception) {
                                println("⚠️ 메시지 파싱 오류: ${e.message}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("❌ WebSocket 연결 실패: ${e.message}")
            } finally {
                isConnected = false
                println("🔌 WebSocket 연결 종료 및 플래그 초기화")
            }
        }
    }

    // 단일 명령 처리 함수
    private fun handleCommand(command: Component) {
        _components.update { currentList ->
            val newList = currentList.toMutableList()
            when (command.action) {
                ComponentAction.Create, ComponentAction.Update -> {
                    val index = newList.indexOfFirst { it.id == command.id }
                    if (index != -1) newList[index] = command else newList.add(command)
                    println("➕ 추가/수정됨: ${command.id}")
                }
                ComponentAction.Delete -> {
                    newList.removeIf { it.id == command.id }
                    println("🗑️ 삭제됨: ${command.id}")
                }
                else -> {}
            }
            newList
        }
    }

    fun scrollCanvas(dx: Float, dy: Float) {
        _canvasScrollState.update { (currentX, currentY) ->
            Pair(currentX + dx, currentY + dy)
        }
    }

    fun resetCanvas() {
        _canvasScrollState.value = Pair(0f, 0f)
    }
}
