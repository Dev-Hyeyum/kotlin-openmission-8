package com.example.kotlin_openmission_8.model

import android.graphics.Bitmap
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
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

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

    suspend fun uploadThumbnail(roomId: String, bitmap: Bitmap) {
        withContext(Dispatchers.IO) { // 백그라운드 스레드에서 실행
            try {
                // 1. Bitmap -> ByteArray (PNG) 변환
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream) // 품질 80
                val byteArray = stream.toByteArray()

                // 2. 서버로 전송
                client.post("$BASE_URL/upload-thumbnail/$roomId") {
                    setBody(
                        MultiPartFormDataContent(
                        formData {
                            append("image", byteArray, Headers.build {
                                append(HttpHeaders.ContentType, "image/png")
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"thumbnail.png\""
                                )
                            })
                        }
                    ))
                }
                println("썸네일 업로드 성공")
            } catch (e: Exception) {
                println("썸네일 업로드 실패: ${e.message}")
            }
        }
    }

    // sideBar 상태
    private val _isSideBarExpanded = MutableStateFlow(true) // 초기값 true
    val isSideBarExpanded: StateFlow<Boolean> = _isSideBarExpanded.asStateFlow()
    // 사이드바의 화면 상태
    private val _isSideBarMenu = MutableStateFlow(true)
    val isSideBarMenu: StateFlow<Boolean> = _isSideBarMenu.asStateFlow()

    private var webSocketJob: Job? = null // ✨ 2. WebSocket 작업을 저장할 변수

    // ✨ 1. [추가] "로비"가 보여줄 캔버스(룸) 목록 상태
    private val _roomList = MutableStateFlow<List<String>>(emptyList())
    val roomList: StateFlow<List<String>> = _roomList.asStateFlow()


    fun createCanvas() {
        viewModelScope.launch {
            try {
                // /create-canvas API를 호출
                val response: CreateCanvasResponse =
                    client.post("${BASE_URL}/create-canvas").body()

                println("새 캔버스 생성 성공: ${response.roomId}")
                fetchRoomList()
            }catch (e: Exception) {
                println("새 캔버스 생성 실패: ${e.message}")
            }
        }
    }

    fun fetchRoomList() {
        viewModelScope.launch {
            try {
                // 1번에서 만든 서버 API 호출
                val list = client.get("${BASE_URL}/rooms").body<List<String>>()
                _roomList.value = list
                println("룸 목록 로드 성공: ${list.size}개")
            } catch (e: Exception) {
                println("룸 목록 로드 실패: ${e.message}")
            }
        }
    }

    fun deleteCanvas(roomId: String) {
        viewModelScope.launch {
            try {
                // 1. 서버에 DELETE 요청
                client.delete("${BASE_URL}/canvas/$roomId")

                // 2. 삭제 성공 시, 로컬 룸 목록 새로고침
                fetchRoomList()
                println("캔버스 삭제 요청 성공: $roomId")
            } catch (e: Exception) {
                println("캔버스 삭제 요청 실패: ${e.message}")
            }
        }
    }

    fun loadBoard(roomId: String) {
        // 이미 같은 방에 접속해 있다면 중복 실행 방지
        if (isConnected && _currentRoomId.value == roomId) return

        // ✨ 5. [핵심] 기존에 연결된 Job(이전 방)이 있다면 취소!
        webSocketJob?.cancel()

        // 상태 초기화 (새 방에 들어가기 전)
        _components.value = emptyList()
        _component.value = Component(action = ComponentAction.Create, type = ComponentType.Dummy)

        isConnected = true
        _currentRoomId.value = roomId // ⬅️ RoomId를 여기서 설정

        _currentWebUrl.value = "localhost:8080/test.html?room=$roomId"

        // ✨ 6. 새 Job을 시작하고 변수에 저장
        webSocketJob = viewModelScope.launch {
            try {
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

    fun leaveRoom() {
        // WebSocket Job을 취소시킴 (위의 finally 블록이 실행됨)
        webSocketJob?.cancel()
        webSocketJob = null

        // ✅ [추가] 뒤로 가기를 누르는 즉시 ID를 null로 만듭니다.
        _currentRoomId.value = null
        _currentWebUrl.value = null

        // UI도 즉시 초기화
        _components.value = emptyList()
        _component.value = Component(action = ComponentAction.Create, type = ComponentType.Dummy)
        _canvasScrollState.value = Pair(0f, 0f)
        println("🚪 방을 나갑니다.")
    }

    private suspend fun sendCommand(component: Component, logTag: String) {
        val roomId = _currentRoomId.value ?: run {
            println("$logTag 실패: Room ID가 없습니다.")
            return
        }

        try {
            val response = client.post("$BASE_URL/command/$roomId") {
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

            val updatedComponent = _components.value.firstOrNull { it.id == id }

            if (updatedComponent == null) {
                println("❌ updateComponent 실패: ID(${id})를 리스트에서 찾을 수 없습니다.")
                return@launch // 버그 방지를 위해 함수 종료
            }
            if (_component.value.id == id) {
                _component.value = updatedComponent
            }

            val updateCommand = updatedComponent.copy(action = ComponentAction.Update)

            sendCommand(updateCommand, "수정")
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
