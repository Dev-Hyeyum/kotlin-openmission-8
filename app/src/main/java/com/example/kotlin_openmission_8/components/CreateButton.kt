package com.example.kotlin_openmission_8.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kotlin_openmission_8.model.Component
import com.example.kotlin_openmission_8.model.ComponentAction
import com.example.kotlin_openmission_8.model.ComponentType
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CreateButton(coroutineScope: CoroutineScope, context: Context, client: HttpClient, type: String) {
    val toast = Toast.makeText(context, type, Toast.LENGTH_SHORT)

    Button(
        modifier = Modifier.padding(10.dp),
        onClick = {
            coroutineScope.launch {
                sendComponentToServer(client)
            }
            toast.show()
        }
    ) {
        Text(text = "$type 생성 버튼")
    }

}

private suspend fun sendComponentToServer(client: HttpClient) {
    // 전송할 데이터 생성
    val newComponent = Component(
        action = ComponentAction.Create,
        type = ComponentType.Button,
        text = "앱에서 보낸 버튼"
    )

    try {
        val response = client.post("http://10.0.2.2:8080/command") {
            contentType(ContentType.Application.Json)
            setBody(newComponent)
        }
        println("전송 성공: ${response.status}")
    } catch (e: Exception) {
        println("전송 실패: ${e.message}")
    }
}