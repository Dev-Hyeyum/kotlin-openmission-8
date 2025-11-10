package com.example.kotlin_openmission_8.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.kotlin_openmission_8.model.Component
import com.example.kotlin_openmission_8.model.ComponentAction
import com.example.kotlin_openmission_8.model.ComponentType // 사용할 Enum 임포트
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SideBarButton(
    coroutineScope: CoroutineScope,
    context: Context,
    client: HttpClient,
    label: String, // UI에 표시될 이름 (예: "텍스트", "버튼")
    componentType: ComponentType, // 서버로 보낼 실제 컴포넌트 타입
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            coroutineScope.launch {
                sendComponentToServer(client, componentType) // 타입 전달
            }
            Toast.makeText(context, "$label 컴포넌트 생성 요청", Toast.LENGTH_SHORT).show()
        },
        modifier = modifier
            .size(60.dp),
        shape = CircleShape,
        // 버튼 색상 정하는 부분
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6DB7B1)
        )
    ) {
        Text(text = label, color = Color.White)
    }
}

private suspend fun sendComponentToServer(client: HttpClient, type: ComponentType) {
    val newComponent = Component(
        action = ComponentAction.Create,
        type = type, // 전달받은 type 사용
        text = "앱에서 보낸 $type 컴포넌트"
    )

    try {
        val response = client.post("http://10.0.2.2:8080/components") {
            contentType(ContentType.Application.Json)
            setBody(newComponent)
        }
        println("전송 성공: ${response.status} - Type: $type")
    } catch (e: Exception) {
        println("전송 실패: ${e.message}")
    }
}