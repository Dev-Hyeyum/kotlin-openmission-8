package com.example.kotlin_openmission_8.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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

@OptIn(ExperimentalMaterial3Api::class)
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
    var selectedEventType by remember { mutableStateOf("SHOW_TOAST") }
    var eventMessage by remember { mutableStateOf("") }
    var eventUrl by remember { mutableStateOf("") }

    val eventOptions = remember { listOf("SHOW_TOAST", "REDIRECT_URL", "NONE") }
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
            title = { Text("버튼 이벤트 및 텍스트 설정") },
            text = {
                Column {
                    // 1. 컴포넌트 텍스트 필드 (Label)
                    TextField(
                        value = writeText,
                        onValueChange = { writeText = it },
                        label = { Text("버튼 텍스트 (Label)") },
                        singleLine = true
                    )

                    if (componentType == ComponentType.Button) {
                        Divider(Modifier.padding(vertical = 8.dp))
                        Text("클릭 이벤트 설정", fontWeight = FontWeight.Bold)

                        // 2. ✨ [필수] 이벤트 타입 선택 드롭다운
                        ExposedDropdownMenuBox(
                            expanded = selectedEventType.contains("EXPANDED"), // 임시 상태
                            onExpandedChange = {
                                // 토글 시 상태에 "EXPANDED"를 임시로 붙여서 확장 상태 제어
                                selectedEventType = if (it) selectedEventType + "EXPANDED" else selectedEventType.replace("EXPANDED", "")
                            },
                            modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
                        ) {
                            TextField(
                                modifier = Modifier.menuAnchor(),
                                readOnly = true,
                                value = selectedEventType.replace("EXPANDED", ""), // EXPANDED 제거 후 표시
                                onValueChange = {},
                                label = { Text("이벤트 종류") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = selectedEventType.contains("EXPANDED")) },
                            )
                            ExposedDropdownMenu(
                                expanded = selectedEventType.contains("EXPANDED"),
                                onDismissRequest = { selectedEventType = selectedEventType.replace("EXPANDED", "") }
                            ) {
                                eventOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            // 3. ✨ 이벤트 타입을 바꾸면 아래 필드가 바뀝니다.
                                            selectedEventType = option
                                        }
                                    )
                                }
                            }
                        }

                        // 4. ✨ [조건부] 선택된 타입에 따라 다른 필드 표시
                        when (selectedEventType.replace("EXPANDED", "")) {
                            "SHOW_TOAST" -> TextField(
                                value = eventMessage,
                                onValueChange = { eventMessage = it },
                                label = { Text("토스트 메시지 내용") },
                                singleLine = true
                            )
                            "REDIRECT_URL" -> TextField(
                                value = eventUrl,
                                onValueChange = { eventUrl = it },
                                label = { Text("이동할 URL") },
                                singleLine = true
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val buttonStyle = ComponentStyle(
                            backgroundColor = "#FF6DB7B1", // 사이드바 버튼 색상과 유사
                            fontColor = "#FFFFFFFF",       // 흰색 글씨
                            borderRadius = 10.0f,         // 둥근 모서리 고정
                        )

                        val action: EventAction? = when (selectedEventType.replace("EXPANDED", "")) {
                            "SHOW_TOAST" -> EventAction(type = "SHOW_TOAST", message = eventMessage)
                            "REDIRECT_URL" -> EventAction(type = "REDIRECT_URL", targetUrl = eventUrl)
                            else -> null // NONE일 경우 Action 객체를 생성하지 않음
                        }

                        val newComponent = Component(
                            action = ComponentAction.Create,
                            type = componentType,
                            text = writeText,
                            onClickAction = action,
                            style = buttonStyle
                        )

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
