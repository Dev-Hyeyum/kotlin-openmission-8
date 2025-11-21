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
import com.example.kotlin_openmission_8.model.ComponentType
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
    var isDropdownExpanded by remember { mutableStateOf(false) } // 드롭다운 상태

    val eventOptions = remember { listOf("SHOW_TOAST", "OPEN_LINK", "NONE") }
    val scope = rememberCoroutineScope()

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
            title = { Text(if (componentType == ComponentType.Button) "버튼 설정" else "텍스트 설정") },
            text = {
                Column {
                    // 컴포넌트 텍스트 필드 (Label)
                    TextField(
                        value = writeText,
                        onValueChange = { writeText = it },
                        label = { Text("컴포넌트 텍스트 (Label)") },
                        singleLine = true
                    )

                    if (componentType == ComponentType.Button) {
                        Divider(Modifier.padding(vertical = 8.dp))
                        Text("클릭 이벤트 설정", fontWeight = FontWeight.Bold)

                        // 이벤트 타입 선택 드롭다운
                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                            modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
                        ) {
                            TextField(
                                modifier = Modifier.menuAnchor(),
                                readOnly = true,
                                value = selectedEventType,
                                onValueChange = {},
                                label = { Text("동작 선택") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                            )
                            ExposedDropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                eventOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedEventType = option
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        when (selectedEventType) {
                            "SHOW_TOAST" -> TextField(
                                value = eventMessage,
                                onValueChange = { eventMessage = it },
                                label = { Text("토스트 메시지 내용") },
                                singleLine = true
                            )
                            "OPEN_LINK", "REDIRECT_URL" -> TextField(
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
                        val finalStyle = if (componentType == ComponentType.Button) {
                            // 버튼일 때: 청록색 배경, 둥근 모서리
                            ComponentStyle(
                                backgroundColor = "#FF6DB7B1",
                                fontColor = "#FFFFFFFF",
                                borderRadius = 10.0f,
                            )
                        } else {
                            // 텍스트일 때: 투명 배경, 직각 모서리, 검정 글씨
                            ComponentStyle(
                                backgroundColor = "#FFFFFFFF", // 흰색
                                fontColor = "#FF000000",       // 검정색
                                borderRadius = 0.0f,           // 직각
                            )
                        }

                        // 버튼일 때만 액션 리스트 생성
                        val newActions = if (componentType == ComponentType.Button && selectedEventType != "NONE") {
                            val actionValue = if (selectedEventType == "SHOW_TOAST") eventMessage else eventUrl
                            listOf(
                                EventAction(
                                    trigger = "OnClick",
                                    type = selectedEventType,
                                    value = actionValue,
                                    targetId = null
                                )
                            )
                        } else {
                            emptyList()
                        }

                        val newComponent = Component(
                            action = ComponentAction.Create,
                            type = componentType,
                            text = writeText,
                            style = finalStyle,
                            actions = newActions
                        )

                        viewModel.postComponent(newComponent)
                        showDialog = false
                        writeText = ""
                        eventMessage = ""
                        eventUrl = ""
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
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}