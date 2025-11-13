package com.example.kotlin_openmission_8.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.kotlin_openmission_8.model.Component
import com.example.kotlin_openmission_8.model.ComponentAction
import com.example.kotlin_openmission_8.model.ComponentType // 사용할 Enum 임포트
import com.example.kotlin_openmission_8.model.Components
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SideBarButton(
    coroutineScope: CoroutineScope,
    context: Context,
    viewModel: Components,
    label: String, // UI에 표시될 이름 (예: "텍스트", "버튼")
    componentType: ComponentType, // 서버로 보낼 실제 컴포넌트 타입
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var writeText by remember { mutableStateOf("") }

    Button(
        onClick = {
            // 비동기 요청
            coroutineScope.launch {
                // 버튼 클릭 시 생성 동작
                // 임시
                if (componentType == ComponentType.Text || componentType == ComponentType.Button) {
                    showDialog = true
                } else {
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
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text("텍스트를 적으시오.")
            },
            text = {
                TextField(
                    value = writeText,
                    onValueChange = { newText ->
                        writeText = newText
                    },
                    label = { Text("텍스트를 입력해주세요") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            val newComponent = Component(
                                action = ComponentAction.Create,
                                type = componentType,
                                text = writeText // ✅ 입력 완료 후 서버로 전송
                            )
                            viewModel.postComponent(newComponent)
                        }
                        showDialog = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // 취소 버튼 클릭 시 수행할 작업
                        showDialog = false
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }
}
