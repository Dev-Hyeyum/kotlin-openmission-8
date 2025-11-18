package com.example.kotlin_openmission_8.components

import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_openmission_8.model.Component
import com.example.kotlin_openmission_8.model.Components
import kotlin.math.roundToInt
import androidx.core.graphics.toColorInt
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

    val selectedComponent by viewModel.component.collectAsState()
    val isSelected = component.id == selectedComponent.id

    // 외부(서버/ViewModel)에서 데이터가 변경되면 내부 상태도 갱신
    // component 키값이 바뀌면(즉, 리스트 내용이 갱신되면) 이 블록이 실행
    LaunchedEffect(component) {
        offsetX = component.offsetX
        offsetY = component.offsetY
        boxWidth = component.width
        boxHeight = component.height
        text = component.text
    }

    // dp 단위로 변경
    val boxWidthDp = with(density) { boxWidth.toDp() }
    val boxHeightDp = with(density) { boxHeight.toDp() }
    // 사용자가 적게 움직였을 때 값을 무시하는 객체
    val touchSlop = LocalViewConfiguration.current.touchSlop

    // ViewModel의 스타일 데이터를 Compose 객체로
    val styleData = component.style
    // dp 단위로 변경
    val composeBorderRadius = styleData.borderRadius.dp
    // String -> Color (배경색)
    val composeBackgroundColor = remember(styleData.backgroundColor) {
        try { Color(parseColor(styleData.backgroundColor)) }
        catch (e: Exception) { Color.Gray } // 잘못된 값일 경우 기본값
    }

    // String -> Color (글꼴색)
    val composeFontColor = remember(styleData.fontColor) {
        try { Color(parseColor(styleData.fontColor)) }
        catch (e: Exception) { Color.Black } // 기본값
    }

    // Float -> TextUnit (글꼴 크기)
    val composeFontSize = with(density) {
        styleData.fontSize.toSp()
    }
    // String -> FontWeight (글꼴 두께)
    val composeFontWeight = remember(styleData.fontWeight) {
        when (styleData.fontWeight) {
            "Bold" -> FontWeight.Bold
            "Medium" -> FontWeight.Medium
            "Light" -> FontWeight.Light
            else -> FontWeight.Normal
        }
    }

    // String -> FontFamily (글꼴)
    val composeFontFamily = remember(styleData.fontFamily) {
        when (styleData.fontFamily) {
            "Serif" -> FontFamily.Serif
            "Monospace" -> FontFamily.Monospace
            else -> FontFamily.Default
        }
    }

    val composeBorderColor = remember(styleData.borderColor) {
        try { Color(styleData.borderColor.toColorInt()) }
        catch (e: Exception) { Color.Gray } // 기본값
    }
    val handleResize = { alignment: Alignment, dragAmount: Offset ->
        val bias = alignment as? BiasAlignment
        val hBias = bias?.horizontalBias ?: 0f // -1(Left), 0, 1(Right)
        val vBias = bias?.verticalBias ?: 0f   // -1(Top), 0, 1(Bottom)

        // 1. 가로 크기/위치 조절
        if (hBias == -1f) { // 왼쪽 핸들
            val newWidth = (boxWidth - dragAmount.x).coerceAtLeast(50f)
            val widthChange = boxWidth - newWidth
            if (newWidth > 50f) {
                offsetX += widthChange
                boxWidth = newWidth
            }
        } else if (hBias == 1f) { // 오른쪽 핸들
            boxWidth = (boxWidth + dragAmount.x).coerceAtLeast(50f)
        }

        // 2. 세로 크기/위치 조절
        if (vBias == -1f) { // 위쪽 핸들
            val newHeight = (boxHeight - dragAmount.y).coerceAtLeast(50f)
            val heightChange = boxHeight - newHeight
            if (newHeight > 50f) {
                offsetY += heightChange
                boxHeight = newHeight
            }
        } else if (vBias == 1f) { // 아래쪽 핸들
            boxHeight = (boxHeight + dragAmount.y).coerceAtLeast(50f)
        }

        // 3. 실시간 서버 업데이트 (드래그 중에도 반영)
        viewModel.updateComponent(
            id = component.id,
            offsetX = offsetX,
            offsetY = offsetY,
            width = boxWidth,
            height = boxHeight
        )
    }
    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(boxWidthDp, boxHeightDp)
            .border(
                1.dp,
                composeBorderColor,
                shape = RoundedCornerShape(composeBorderRadius),
                )
            .background(
                composeBackgroundColor,
                shape = RoundedCornerShape(composeBorderRadius)
            )
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
                            offsetX = (offsetX + panChange.x).coerceAtLeast(0f)
                            offsetY = (offsetY + panChange.y).coerceAtLeast(0f)

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
                        viewModel.getComponent(component.id)
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
        Text(
            text = "${component.type} $text",
            color = composeFontColor,
            fontSize = composeFontSize,
            fontWeight = composeFontWeight,
            fontFamily = composeFontFamily
        )

        if (isSelected) {
            Handle(Alignment.TopStart, onDrag = { handleResize(Alignment.TopStart, it) })
            Handle(Alignment.TopCenter, onDrag = { handleResize(Alignment.TopCenter, it) })
            Handle(Alignment.TopEnd, onDrag = { handleResize(Alignment.TopEnd, it) })
            Handle(Alignment.CenterStart, onDrag = { handleResize(Alignment.CenterStart, it) })
            Handle(Alignment.CenterEnd, onDrag = { handleResize(Alignment.CenterEnd, it) })
            Handle(Alignment.BottomStart, onDrag = { handleResize(Alignment.BottomStart, it) })
            Handle(Alignment.BottomCenter, onDrag = { handleResize(Alignment.BottomCenter, it) })
            Handle(Alignment.BottomEnd, onDrag = { handleResize(Alignment.BottomEnd, it) })
        }
    }
}

@Composable
private fun BoxScope.Handle(
    alignment: Alignment,
    size: Dp = 8.dp,
    color: Color = Color.Green,
    onDrag: (Offset) -> Unit
) {
    val bias = alignment as? BiasAlignment ?: return
    Box(
        modifier = Modifier
            .align(alignment)
            .offset(
                x = (size / 2) * bias.horizontalBias,
                y = (size / 2) * bias.verticalBias
            )
            .size(size)
            .clip(CircleShape) // 원 모양 클립
            .background(color) // 배경색
            .border(1.dp, Color.White, CircleShape) // 흰색 테두리 추가 (가시성 확보)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume() // 이벤트 소비 (부모 드래그 방지)
                    onDrag(dragAmount) // 리사이징 로직 실행
                }
            }
    )
}