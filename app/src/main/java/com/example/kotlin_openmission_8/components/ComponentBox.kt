package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.kotlin_openmission_8.model.Component
import kotlin.math.roundToInt

@Composable
fun ComponentBox(
    component: Component,
    parentWidth: Int,
    parentHeight: Int,
    onPositionChange: (Float, Float) -> Unit,
    onSizeChange: ((Float, Float) -> Unit)? = null // 사이즈 변경 콜백 (선택)
) {
    val density = LocalDensity.current

    // 초기 크기를 컴포넌트 데이터에서 불러올 수도 있음
    var offsetX by remember { mutableStateOf(component.offsetX) }
    var offsetY by remember { mutableStateOf(component.offsetY) }

    var boxWidth by remember { mutableStateOf(component.width) }
    var boxHeight by remember { mutableStateOf(component.height) }

    val boxWidthDp = with(density) { boxWidth.toDp() }
    val boxHeightDp = with(density) { boxHeight.toDp() }

    var showInfo by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(boxWidthDp, boxHeightDp)
            .background(Color.Blue)
            .clickable {
                println(component)
                showInfo = true
            }
            // 이동 제스처
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // 이동
                    offsetX = (offsetX + pan.x).coerceIn(0f, parentWidth - boxWidth)
                    offsetY = (offsetY + pan.y).coerceIn(0f, parentHeight - boxHeight)

                    // 크기 조절 (줌 값 적용)
                    if (zoom != 1f) {
                        boxWidth = (boxWidth * zoom).coerceIn(50f, parentWidth.toFloat())
                        boxHeight = (boxHeight * zoom).coerceIn(50f, parentHeight.toFloat())
                    }

                    onPositionChange(offsetX, offsetY)
                    onSizeChange?.invoke(boxWidth, boxHeight)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text("${component.type} ${component.text}", color = Color.White)
    }

    if (showInfo) {
        AlertDialog(
            onDismissRequest = {
                showInfo = false
            },
            title = {
                Text("정보")
            },
            text = {
                Column {
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
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newWidth = boxWidth
                        val newHeight = boxHeight
                        val newOffsetX = offsetX
                        val newOffsetY = offsetY

                        onPositionChange(newOffsetX, newOffsetY)
                        onSizeChange?.invoke(newWidth, newHeight)
                        showInfo = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showInfo = false
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }
}
