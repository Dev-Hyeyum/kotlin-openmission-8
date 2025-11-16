package com.example.kotlin_openmission_8.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_openmission_8.BuildConfig
import com.example.kotlin_openmission_8.BuildConfig.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
import kotlinx.serialization.Serializable
import io.ktor.client.call.*
import io.ktor.client.request.*

@Serializable
private data class CreateCanvasResponse(
    val roomId: String,
    val url: String
)

class Components(private val client: HttpClient): ViewModel() {
    // 컴포넌트의 상태
    private val _components = MutableStateFlow<List<Component>>(emptyList())
    val components: StateFlow<List<Component>> = _components.asStateFlow()

    // MainContentArea 의 상태
    private val _canvasScrollState = MutableStateFlow(Pair(0f, 0f))
    val canvasScrollState: StateFlow<Pair<Float, Float>> = _canvasScrollState.asStateFlow()

    // 선택한 컴포넌트
    private val _component = MutableStateFlow(Component(action = ComponentAction.Create, type = ComponentType.Dummy))
    val component: StateFlow<Component> = _component.asStateFlow()

    // 현재 입장한 캔버스의 ID
    private val _currentRoomId = MutableStateFlow<String?>(null)
    val currentRoomId: StateFlow<String?> = _currentRoomId.asStateFlow()
    // 사용자에게 제공된 웹 URL
    private val _currentWebUrl = MutableStateFlow<String?>(null)
    val currentWebUrl: StateFlow<String?> = _currentWebUrl.asStateFlow()

    // websocket 접속 상태
    private var isConnected = false

    // sideBar 상태
    private val _isSideBarExpanded = MutableStateFlow(true) // 초기값 true
    val isSideBarExpanded: StateFlow<Boolean> = _isSideBarExpanded.asStateFlow()
    // 사이드바의 화면 상태
    private val _isSideBarMenu = MutableStateFlow(true)
    val isSideBarMenu: StateFlow<Boolean> = _isSideBarMenu.asStateFlow()


    fun createCanvas() {
        viewModelScope.launch {
            try {
                // /create-canvas API를 호출
                val response: CreateCanvasResponse =
                    client.post("${BASE_URL}/create-canvas").body()
                _currentRoomId.value = response.roomId
                _currentWebUrl.value = response.url

                println("새 캔버스 생성 성공: ${response.roomId}")

                connectWebSocket(response.roomId)
            }catch (e: Exception) {
                    println("새 캔버스 생성 실패: ${e.message}")
                }
            }
        }

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

    fun getComponent(id: String) {
        _component.value = _components.value.first { it.id == id }
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
        text: String? = null,
        style: ComponentStyle? = null
    ) {
        viewModelScope.launch {
            _components.update { current ->
                current.map { component ->
                    if (component.id == id) {
                        component.copy(
                            text = text ?: component.text,
                            width = width ?: component.width,
                            height = height ?: component.height,
                            offsetX = (offsetX ?: component.offsetX).coerceAtLeast(0f),
                            offsetY = (offsetY ?: component.offsetY).coerceAtLeast(0f),
                            style = style ?: component.style
                        )
                    } else {
                        component
                    }
                }
            }

            val updatedComponent = _components.value.first { it.id == id }

            if (_component.value.id == id) {
                _component.value = updatedComponent
            }

            val updateCommand = updatedComponent.copy(action = ComponentAction.Update)

            sendCommand(updateCommand, "수정")
        }
    }

    // 이제 roomId를 인자로 받도록 수정했습니다.
    fun connectWebSocket(roomId: String) {
        if(isConnected) return
        isConnected = true

        viewModelScope.launch {
            try {
                // URL을 RoomId를 사용하도록 변경
                client.webSocket("$WS_URL/$roomId") {
                    println("✅ WebSocket 연결 성공 (Room: $roomId)")
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val message = frame.readText()
                            try {
                                if (message.trim().startsWith("[")) {
                                    // 1. 초기 전체 리스트 수신
                                    val allComponents = Json.decodeFromString<List<Component>>(message)
                                    _components.value = allComponents
                                    println("📦 초기 데이터 로드 완료: ${allComponents.size}개")

                                    if (allComponents.isNotEmpty()) {
                                        _component.value = allComponents.first()
                                    }
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
                _currentRoomId.value = null
                _currentWebUrl.value = null
                println("🔌 WebSocket 연결 종료 및 플래그 초기화")
            }
        }
    }

    // 단일 명령 처리 함수
    private fun handleCommand(command: Component) {
        _components.update { currentList ->
            val newList = currentList.toMutableList()

            val cleanCommand = command.copy(
                offsetX = command.offsetX.coerceAtLeast(0f),
                offsetY = command.offsetY.coerceAtLeast(0f)
            )

            when (command.action) {
                ComponentAction.Create, ComponentAction.Update -> {


                    val index = newList.indexOfFirst { it.id == cleanCommand.id }
                    if (index != -1) newList[index] = cleanCommand else newList.add(cleanCommand)
                    println("➕ 추가/수T-수정됨: ${cleanCommand.id}")
                }
                ComponentAction.Delete -> {
                    newList.removeIf { it.id == command.id }
                    println("🗑️ 삭제됨: ${command.id}")
                }
            }
            if (_component.value.id == cleanCommand.id && command.action != ComponentAction.Delete) {
                _component.value = cleanCommand
            }
            newList
        }
    }

    fun scrollCanvas(dx: Float, dy: Float) {
        _canvasScrollState.update { (currentX, currentY) ->
            val newX = currentX + dx
            val newY = currentY + dy

            Pair(newX.coerceAtMost(0f), newY.coerceAtMost(0f))
        }
    }

    fun resetCanvas() {
        _canvasScrollState.value = Pair(0f, 0f)
    }

    fun showSideBar() {
        _isSideBarExpanded.value = true
    }

    fun notShowSideBar() {
        _isSideBarExpanded.value = false
    }

    fun isCreateMenu() {
        _isSideBarMenu.value = true
    }

    fun isEditMenu() {
        _isSideBarMenu.value = false
    }

    companion object {
        // 서버 URL
        private const val BASE_URL = BuildConfig.BASE_URL
        private const val WS_URL = BuildConfig.WS_URL
    }
}
