package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kotlin_openmission_8.model.ComponentType
import com.example.kotlin_openmission_8.model.Components

@Composable
fun CreateMenu(
    viewModel: Components
) {
    val component by viewModel.component.collectAsState()

    // component의 x,y 값 데이터를 불러옴
    var offsetX by remember { mutableFloatStateOf(component.offsetX) }
    var offsetY by remember { mutableFloatStateOf(component.offsetY) }
    // component의 높이와 너비 데이터를 불러옴
    var boxWidth by remember { mutableFloatStateOf(component.width) }
    var boxHeight by remember { mutableFloatStateOf(component.height) }
    // component의 텍스트 데이터를 불러옴
    var text by remember { mutableStateOf(component.text) }

    // 외부(서버/ViewModel)에서 데이터가 변경되면 내부 상태도 갱신
    // component 키값이 바뀌면(즉, 리스트 내용이 갱신되면) 이 블록이 실행
    LaunchedEffect(component) {
        offsetX = component.offsetX
        offsetY = component.offsetY
        boxWidth = component.width
        boxHeight = component.height
        text = component.text
    }

    Column (
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 스타일 관련 편집 Field 추가하면 됨.
        OutlinedTextField(
            value = boxWidth.toString(),
            onValueChange = { boxWidth = it.toFloatOrNull() ?: boxWidth },
            label = { Text("Width") }
        )
        OutlinedTextField(
            value = boxHeight.toString(),
            onValueChange = { boxHeight = it.toFloatOrNull() ?: boxHeight },
            label = { Text("Height") }
        )
        OutlinedTextField(
            value = offsetX.toString(),
            onValueChange = { offsetX = it.toFloatOrNull() ?: offsetX },
            label = { Text("Offset X") }
        )
        OutlinedTextField(
            value = offsetY.toString(),
            onValueChange = { offsetY = it.toFloatOrNull() ?: offsetY },
            label = { Text("Offset Y") }
        )
        if (component.type == ComponentType.Text || component.type == ComponentType.Button) {
            OutlinedTextField(
                value = text.toString(),
                onValueChange = { text = it },
                label = { Text("Text") }
            )
        }
        Row (
            modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = {
                    viewModel.deleteComponent(component.id)
                }
            ) {
                Text("컴포넌트 삭제")
            }
            TextButton(
                onClick = {
                    viewModel.updateComponent(id = component.id, offsetX = offsetX, offsetY = offsetY, width = boxWidth, height = boxHeight, text = text)
                }
            ) {
                Text("확인")
            }
        }
    }
}