package com.example.kotlin_openmission_8.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Components(private val client: HttpClient): ViewModel() {
    private val _components = MutableStateFlow<List<Component>>(emptyList())
    val components: StateFlow<List<Component>> = _components.asStateFlow()

    private val BASE_URL = "http://10.0.2.2:8080"

    fun getComponent() {
        viewModelScope.launch {
            try {
                // 서버에서 데이터 가져오기
                val serverData: List<Component> = client.get("$BASE_URL/components").body()
                _components.value = serverData
                println("초기 데이터 로드 성공")
            } catch (e: Exception) {
                println("초기 데이터 로드 실패: ${e.message}")
                e.printStackTrace()
                _components.value = listOf(
                    Component(id = "dummy1", action = ComponentAction.Create, type = ComponentType.Button, text = "기본 버튼 (오프라인)"),
                    Component(id = "dummy2", action = ComponentAction.Create, type = ComponentType.Text, text = "서버 연결 안 됨")
                )
            }
        }
    }


    fun postComponent(component: Component) {
        viewModelScope.launch {
            val response = client.post("$BASE_URL/components") {
                contentType(ContentType.Application.Json)
                setBody(component)
            }
            println("전송 성공: ${response.status}")
            getComponent()
        }
    }

    fun deleteComponent(id: String) {
        viewModelScope.launch {
            client.delete("$BASE_URL/$id")
            getComponent()
        }
    }
}