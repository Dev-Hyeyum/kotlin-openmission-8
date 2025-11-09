package com.example.kotlin_openmission_8.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
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

    fun postComponent(component: Component) {
        viewModelScope.launch {
            try {
                val response = client.post("$BASE_URL/command") {
                    contentType(ContentType.Application.Json)
                    setBody(component)
                }
                println("전송 성공: ${response.status}")
            } catch (e: Exception) {
                println("전송 실패: ${e.message}")
            }
        }
    }

    fun deleteComponent(id: String) {
        viewModelScope.launch {
            try {
                val deleteCommand = Component(
                    action = ComponentAction.Delete,
                    type = ComponentType.Text,
                    text = "",
                    id = id
                )

                val response = client.post("$BASE_URL/command") {
                    contentType(ContentType.Application.Json)
                    setBody(deleteCommand)
                }
                println("삭제 요청 성공: ${response.status}")
            } catch (e: Exception) {
                println("삭제 요청 실패: ${e.message}")
            }
        }
    }
}