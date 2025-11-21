package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import coil3.compose.AsyncImage
import com.example.kotlin_openmission_8.BuildConfig
import com.example.kotlin_openmission_8.model.Component
import com.example.kotlin_openmission_8.model.ComponentType
import com.example.kotlin_openmission_8.model.Components
import kotlin.math.roundToInt

@Composable
fun ComponentBox(
    component: Component,
    viewModel: Components
) {
    val density = LocalDensity.current

    // 로컬 상태 (위치/크기)
    var offsetX by remember { mutableFloatStateOf(component.offsetX) }
    var offsetY by remember { mutableFloatStateOf(component.offsetY) }
    var boxWidth by remember { mutableFloatStateOf(component.width) }
    var boxHeight by remember { mutableFloatStateOf(component.height) }
    var text by remember { mutableStateOf(component.text) }

//    val selectedComponent by viewModel.component.collectAsState()
//    val isSelected = component.id == selectedComponent.id
    val currentStyle by rememberUpdatedState(component.style)
    val currentActions by rememberUpdatedState(component.actions)
    val currentImageUrl by rememberUpdatedState(component.imageUrl)

    var showSizeController by remember { mutableStateOf(false) }

    // component를 업데이트하는 변수
    val commitUpdate = {
        viewModel.updateComponent(
            id = component.id,
            offsetX = offsetX,
            offsetY = offsetY,
            width = boxWidth,
            height = boxHeight,
            text = text,
            style = currentStyle,
            actions = currentActions,
            imageUrl = currentImageUrl
        )
    }

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
        try { Color(styleData.backgroundColor.toColorInt()) }
        catch (e: Exception) { Color.Gray } // 잘못된 값일 경우 기본값
    }
    // String -> Color (글꼴색)
    val composeFontColor = remember(styleData.fontColor) {
        try { Color(styleData.fontColor.toColorInt()) }
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
    // component 테두리 색상
    val composeBorderColor = remember(styleData.borderColor) {
        try { Color(styleData.borderColor.toColorInt()) }
        catch (e: Exception) { Color.Gray } // 기본값
    }
    // 드래그로 사이즈 조절
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
                detectTapGestures(
                    onDoubleTap = {
                        showSizeController = !showSizeController
                    }
                )
            }
            .pointerInput(showSizeController) {
                awaitEachGesture {
                    if (showSizeController) {
                        awaitPointerEvent()
                        return@awaitEachGesture
                    }

                    val down = awaitFirstDown()
                    var totalPan = Offset.Zero

                    do {
                        val event = awaitPointerEvent()
                        val panChange = event.calculatePan()

                        // 이동 거리 누적
                        totalPan += panChange
                        val isDragging = totalPan.getDistance() > touchSlop

                        if (isDragging) {
                            // UI 업데이트
                            offsetX = (offsetX + panChange.x).coerceAtLeast(0f)
                            offsetY = (offsetY + panChange.y).coerceAtLeast(0f)

                            event.changes.forEach { it.consume() }
                        }
                    } while (event.changes.any { it.pressed })

                    if (totalPan.getDistance() < touchSlop) {
                        viewModel.isEditMenu()
                        down.consume() // 클릭 이벤트 소비
                    } else {
                        // 드래그 종료 시 업데이트
                        commitUpdate()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        when (component.type) {
            ComponentType.Image if component.imageUrl != null -> {
                // 이미지가 있다면 AsyncImage를 사용하여 로드
                AsyncImage(
                    model = getCorrectedImageUrl(component.imageUrl),
                    contentDescription = component.text ?: "Uploaded Image",
                    modifier = Modifier.fillMaxSize(), // 박스 크기만큼 꽉 채우기
                    contentScale = ContentScale.Crop // 캔버스 크기에 맞게 자르기
                )
            }
            ComponentType.InputField -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White) // 배경은 흰색
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)), // 테두리
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = if (text.isNullOrEmpty()) "입력창 (Input)" else text!!,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 14.sp
                    )
                }
            }
            else -> {
                // Image 타입이 아니거나 URL이 없으면 텍스트를 그림
                Text(
                    text = text ?: "",
                    color = composeFontColor,
                    fontSize = composeFontSize,
                    fontWeight = composeFontWeight,
                    fontFamily = composeFontFamily
                )
            }
        }

        if (showSizeController) {
            Handle(Alignment.TopStart, onDrag = { handleResize(Alignment.TopStart, it) }, onCommit = commitUpdate)
            Handle(Alignment.TopCenter, onDrag = { handleResize(Alignment.TopCenter, it) }, onCommit = commitUpdate)
            Handle(Alignment.TopEnd, onDrag = { handleResize(Alignment.TopEnd, it) }, onCommit = commitUpdate)
            Handle(Alignment.CenterStart, onDrag = { handleResize(Alignment.CenterStart, it) }, onCommit = commitUpdate)
            Handle(Alignment.CenterEnd, onDrag = { handleResize(Alignment.CenterEnd, it) }, onCommit = commitUpdate)
            Handle(Alignment.BottomStart, onDrag = { handleResize(Alignment.BottomStart, it) }, onCommit = commitUpdate)
            Handle(Alignment.BottomCenter, onDrag = { handleResize(Alignment.BottomCenter, it) }, onCommit = commitUpdate)
            Handle(Alignment.BottomEnd, onDrag = { handleResize(Alignment.BottomEnd, it) }, onCommit = commitUpdate)
        }
    }
}

@Composable
private fun BoxScope.Handle(
    alignment: Alignment,
    size: Dp = 8.dp,
    color: Color = Color.Green,
    onDrag: (Offset) -> Unit,
    onCommit: () -> Unit
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
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount) // 1. 로컬 변수(크기/위치)만 변경
                    },
                    onDragEnd = onCommit // 2. 손을 뗄 때만 네트워크 업데이트를 커밋
                )
            }
    )
}

private fun getCorrectedImageUrl(badUrl: String): String {
    return badUrl.replace("http://localhost:8080", BuildConfig.BASE_URL)
}