package com.example.kotlin_openmission_8.components

import android.content.Context
import android.widget.Toast
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.kotlin_openmission_8.model.Component
import com.example.kotlin_openmission_8.model.ComponentAction
import com.example.kotlin_openmission_8.model.ComponentType // 사용할 Enum 임포트
import com.example.kotlin_openmission_8.model.Components
import com.example.kotlin_openmission_8.model.EventAction
import kotlinx.coroutines.CoroutineScope
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

    Button(
        onClick = {
            if (componentType == ComponentType.Text || componentType == ComponentType.Button) {
                showDialog = true
            } else {
                val newComponent = Component(
                    action = ComponentAction.Create,
                    type = componentType
                )
                viewModel.postComponent(newComponent)
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
                    // 2. 기본 텍스트 필드 (Text/Button 모두 사용)
                    TextField(
                        value = writeText,
                        onValueChange = { newText -> writeText = newText },
                        label = { Text("컴포넌트 텍스트 (Label)") },
                        singleLine = true
                    )

                    // ✨ 3. [조건부] Button 타입일 때만 이벤트 입력 필드를 보여줌
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
                        // ✨ 4. [핵심] 컴포넌트 생성 로직 통합
                        val newComponent = if (componentType == ComponentType.Button) {
                            // Button 타입: EventAction 객체 생성 후 포함
                            val action = EventAction(
                                type = "SHOW_TOAST", // (현재는 토스트로 고정)
                                message = eventMessage
                            )
                            Component(
                                action = ComponentAction.Create,
                                type = componentType,
                                text = writeText,
                                onClickAction = action // ✅ EventAction 전달
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
