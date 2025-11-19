package com.example.kotlin_openmission_8.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.kotlin_openmission_8.model.Component
import com.example.kotlin_openmission_8.model.ComponentAction
import com.example.kotlin_openmission_8.model.ComponentStyle
import com.example.kotlin_openmission_8.model.ComponentType // 사용할 Enum 임포트
import com.example.kotlin_openmission_8.model.Components
import com.example.kotlin_openmission_8.model.EventAction
import kotlinx.coroutines.launch

@Composable
fun SideBarButton(
    context: Context,
    viewModel: Components,
    label: String, // UI에 표시될 이름 (예: "텍스트", "버튼")
    componentType: ComponentType, // 서버로 보낼 실제 컴포넌트 타입
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var writeText by remember { mutableStateOf("") }
    var eventMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope() // ✨ CoroutineScope 가져오기

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val bitmap = uriToBitmap(context, it) // URI -> Bitmap 변환 (아래 유틸리티 함수 사용)
                bitmap?.let { btm ->
                    // 2. ViewModel의 업로드 함수 호출
                    viewModel.uploadImageAndCreateComponent(btm)
                }
            }
        }
    }

    Button(
        onClick = {
            when (componentType) {
                ComponentType.Image -> {
                    imageLauncher.launch("image/*") // 갤러리 열기
                    Toast.makeText(context, "$label 컴포넌트 생성 요청 (파일 선택)", Toast.LENGTH_SHORT).show()
                }
                ComponentType.Text, ComponentType.Button -> {
                    showDialog = true
                }
                else -> {
                    val newComponent = Component(
                        action = ComponentAction.Create,
                        type = componentType
                    )
                    viewModel.postComponent(newComponent)
                }
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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("컴포넌트 설정") }, // 타이틀을 더 포괄적으로 변경
            text = {
                Column {
                    TextField(
                        value = writeText,
                        onValueChange = { newText -> writeText = newText },
                        label = { Text("컴포넌트 텍스트 (Label)") },
                        singleLine = true
                    )

                    if (componentType == ComponentType.Button) {
                        Divider(Modifier.padding(vertical = 12.dp))
                        Text("클릭 이벤트 설정", fontWeight = FontWeight.Bold)

                        // 이벤트 메시지 입력 필드 (SHOW_TOAST의 내용)
                        TextField(
                            value = eventMessage,
                            onValueChange = { eventMessage = it },
                            label = { Text("토스트 메시지 내용") },
                            singleLine = true
                        )
                        // (추후 URL 등 다른 EventType 필드 추가 가능)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newComponent = if (componentType == ComponentType.Button) {
                            val buttonStyle = ComponentStyle(
                                backgroundColor = "#FF6DB7B1", // 연한 청록색 배경
                                fontColor = "#FFFFFFFF", // 흰색 글씨
                                borderRadius = 12.0f, // 12px 둥근 모서리
                            )

                            val action = EventAction(
                                type = "SHOW_TOAST", // (현재는 토스트로 고정)
                                message = eventMessage
                            )
                            Component(
                                action = ComponentAction.Create,
                                type = componentType,
                                text = writeText,
                                onClickAction = action,
                                style = buttonStyle
                            )
                        } else {
                            // Text 타입/기타: 기본 텍스트만 전달
                            Component(
                                action = ComponentAction.Create,
                                type = componentType,
                                text = writeText
                            )
                        }

                        viewModel.postComponent(newComponent)
                        showDialog = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("취소")
                }
            }
        )
    }
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        // ContentResolver를 사용하여 URI의 스트림을 열고 Bitmap으로 디코딩
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
