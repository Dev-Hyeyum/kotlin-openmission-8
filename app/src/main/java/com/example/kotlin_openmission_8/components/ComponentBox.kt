package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.IntOffset
import com.example.kotlin_openmission_8.model.Component
import com.example.kotlin_openmission_8.model.ComponentType
import com.example.kotlin_openmission_8.model.Components
import kotlin.math.roundToInt

@Composable
fun ComponentBox(
    component: Component,
    viewModel: Components
) {
    // 화면 밀도 데이터를 가지고 있는 객체, dp <-> px 를 변화할 때 사용
    val density = LocalDensity.current
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

    // component의 높이와 너비를 dp 단위로 변경
    val boxWidthDp = with(density) { boxWidth.toDp() }
    val boxHeightDp = with(density) { boxHeight.toDp() }
    // AlertDialog의 보여줌을 나타내는 Boolean 값
    var showInfo by remember { mutableStateOf(false) }
    // 사용자가 적게 움직였을 때 값을 무시하는 객체
    val touchSlop = LocalViewConfiguration.current.touchSlop

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(boxWidthDp, boxHeightDp)
            .background(Color.Blue)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    var totalPan = Offset.Zero // 총 이동 거리 누적
                    var isZooming = false

                    do {
                        val event = awaitPointerEvent()
                        val panChange = event.calculatePan()
                        val zoomChange = event.calculateZoom()

                        // 이동 거리 누적
                        totalPan += panChange

                        // 줌이 발생했거나, 총 이동 거리가 touchSlop을 넘으면 드래그/줌으로 간주
                        val isDragging = totalPan.getDistance() > touchSlop
                        if (zoomChange != 1f) isZooming = true

                        if (isDragging || isZooming) {
                            // === 드래그 또는 줌 동작 수행 ===

                            // UI 업데이트
                            offsetX += panChange.x
                            offsetY += panChange.y

                            if (zoomChange != 1f) {
                                boxWidth = (boxWidth * zoomChange).coerceAtLeast(50f)
                                boxHeight = (boxHeight * zoomChange).coerceAtLeast(50f)
                            }

                            // 이벤트를 소비하여 다른 요소가 처리하지 않도록 함
                            event.changes.forEach { it.consume() }
                        }

                    } while (event.changes.any { it.pressed })

                    // === 손을 뗐을 때 판별 ===

                    // 1. 이동 거리가 짧고 줌도 안 했다면 -> 클릭으로 간주!
                    if (totalPan.getDistance() < touchSlop && !isZooming) {
                        showInfo = true // 정보창 띄우기
                    }
                    // 2. 드래그나 줌을 했다면 -> 서버 업데이트
                    else {
                        viewModel.updateComponent(
                            id = component.id,
                            offsetX = offsetX,
                            offsetY = offsetY,
                            width = boxWidth,
                            height = boxHeight,
                            text = text
                        )
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text("${component.type} $text", color = Color.White)
    }

    if (showInfo) {
        // CSS 값을 기억할 state
        var backgroundColor by remember { mutableStateOf(component.style?.get("backgroundColor") ?: "#FFFFFF") }
        var color by remember { mutableStateOf(component.style?.get("color") ?: "#000000")}
        var fontSize by remember { mutableStateOf(component.style?.get("fontSize") ?: "16")}
        var fontWeight by remember { mutableStateOf(component.style?.get("fontWeight") ?: "normal") }
        var fontFamily by remember { mutableStateOf(component.style?.get("fontFamily") ?: "sans-serif") }
        AlertDialog(
            onDismissRequest = {
                showInfo = false
            },
            title = {
                Text("정보")
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
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
                    // 배경색 입력
                    OutlinedTextField(
                        value = backgroundColor,
                        onValueChange = { backgroundColor = it },
                        label = { Text("배경색 (예: #FF0000)") }
                    )

                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("글자색 (예: #000000)") }
                    )

                    OutlinedTextField(
                        value = fontSize,
                        onValueChange = { fontSize = it },
                        label = { Text("글자 크기 (px 단위 숫자)") }
                    )

                    OutlinedTextField(
                        value = fontWeight,
                        onValueChange = { fontWeight = it },
                        label = { Text("글자 두께 (bold / normal)") }
                    )

                    OutlinedTextField(
                        value = fontFamily,
                        onValueChange = { fontFamily = it },
                        label = { Text("폰트 (serif / sans-serif)") }
                    )
                    Button(
                        onClick = {
                            viewModel.deleteComponent(component.id)
                            showInfo = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("컴포넌트 삭제")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 스타일 맵 생성
                        val newStyleMap = mapOf(
                            "backgroundColor" to backgroundColor,
                            "color" to color,
                            "fontSize" to fontSize,
                            "fontWeight" to fontWeight,
                            "fontFamily" to fontFamily,
                        )
                        viewModel.updateComponent(
                            id = component.id,
                            offsetX = offsetX,
                            offsetY = offsetY,
                            width = boxWidth,
                            height = boxHeight,
                            text = text,
                            // 스타일 추가
                            style = newStyleMap
                        )
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
