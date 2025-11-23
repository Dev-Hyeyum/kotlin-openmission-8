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
import com.example.kotlin_openmission_8.model.EventType
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
    var selectedEventType by remember { mutableStateOf(EventType.SHOW_TOAST) }
    var eventMessage by remember { mutableStateOf("") }
    var eventUrl by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) } // 드롭다운 상태

    val eventOptions = remember { EventType.entries }
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
                ComponentType.Text, ComponentType.Button, ComponentType.InputField, ComponentType.Dropdown -> {
                    showDialog = true
                }
                else -> {}
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
                                value = selectedEventType.label,
                                onValueChange = {},
                                label = { Text("이벤트 종류") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                            )
                            ExposedDropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                eventOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.label) },
                                        onClick = {
                                            selectedEventType = option
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        when (selectedEventType) {
                            EventType.SHOW_TOAST -> TextField(
                                value = eventMessage,
                                onValueChange = { eventMessage = it },
                                label = { Text("토스트 메시지 내용") },
                                singleLine = true
                            )
                            EventType.OPEN_LINK -> TextField(
                                value = eventUrl,
                                onValueChange = { eventUrl = it },
                                label = { Text("이동할 URL") },
                                singleLine = true
                            )
                            else -> { /* NONE 등 처리 없음 */ }
                        }
                    } else if(componentType == ComponentType.Dropdown) {
                        var choiceNumber by remember { mutableStateOf(1) }
                        var dropdownChoices by remember { mutableStateOf(List(choiceNumber) { "" }) }

                        Text("선택지의 개수")
                        IncreaseDecreaseBox(value = choiceNumber.toFloat(), onChangeValue = { newFloatValue ->
                            val newInt = newFloatValue.toInt().coerceAtLeast(1) // 1 미만 방지

                            // ✨ [핵심] 개수가 변경되면 리스트의 크기를 조정합니다.
                            if (newInt != choiceNumber) {
                                choiceNumber = newInt
                                dropdownChoices = List(newInt) { index ->
                                    // 기존 값은 유지하고, 새롭게 추가된 부분은 빈 문자열("")로 채웁니다.
                                    dropdownChoices.getOrElse(index) { "" }
                                }}})
                        Column(Modifier.padding(top = 16.dp)) {
                            (0 until choiceNumber).forEach { index ->
                                TextField(
                                    value = dropdownChoices[index],
                                    onValueChange = { newValue ->
                                        // 해당 인덱스의 값만 새로운 값으로 업데이트
                                        dropdownChoices = dropdownChoices.toMutableList().apply {
                                            this[index] = newValue
                                        }
                                    },
                                    label = { Text("선택지 ${index + 1} 내용") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val finalStyle = when (componentType) {
                            ComponentType.Button -> {
                                // 버튼일 때: 청록색 배경, 둥근 모서리
                                ComponentStyle(
                                    backgroundColor = "#FF6DB7B1",
                                    fontColor = "#FFFFFFFF",
                                    borderRadius = 10.0f,
                                )
                            }
                            ComponentType.InputField -> {
                                ComponentStyle(
                                    backgroundColor = "#FFFFFFFF", // 흰색 배경
                                    fontColor = "#FF000000",       // 검정 글씨
                                    borderColor = "#FF888888",     // 회색 테두리
                                )
                            }
                            ComponentType.Dropdown -> {
                                ComponentStyle(
                                    backgroundColor = "#FFFFFFFF", // 흰색 배경
                                    fontColor = "#FF000000",       // 검정 글씨
                                    borderColor = "#FF888888",     // 회색 테두리
                                )
                            }
                            else -> {
                                // 텍스트일 때: 투명 배경, 직각 모서리, 검정 글씨
                                ComponentStyle(
                                    backgroundColor = "#FFFFFFFF", // 흰색
                                    fontColor = "#FF000000",       // 검정색
                                    borderRadius = 0.0f,           // 직각
                                )
                            }
                        }

                        val newActions = if (componentType == ComponentType.Button && selectedEventType != EventType.NONE) {
                            val actionValue = when (selectedEventType) {
                                EventType.SHOW_TOAST -> eventMessage
                                EventType.OPEN_LINK -> eventUrl
                                else -> ""
                            }

                            listOf(
                                EventAction(
                                    trigger = "OnClick",
                                    type = selectedEventType.name, // [수정] Enum -> String 변환
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
                            style = finalStyle, // [수정] 분기된 스타일 적용
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