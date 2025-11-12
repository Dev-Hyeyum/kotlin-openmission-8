package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
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
    onPositionChange: (Float, Float) -> Unit
) {
    val density = LocalDensity.current

    val childBoxSize = 100.dp
    val childBoxSizePx = with(density) { childBoxSize.toPx() }

    var offsetX by remember { mutableStateOf(component.offsetX) }
    var offsetY by remember { mutableStateOf(component.offsetY) }

    Box(
        modifier = Modifier
            // 2. 현재 좌표만큼 박스를 이동시켜서 그림
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(childBoxSize)
            .background(Color.Blue)
            // 3. 드래그 제스처 감지
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    // 4. 드래그한 만큼 좌표 업데이트
                    offsetX = (offsetX + dragAmount.x).coerceIn(0f, parentWidth - childBoxSizePx)
                    offsetY = (offsetY + dragAmount.y).coerceIn(0f, parentHeight - childBoxSizePx)

                    onPositionChange(offsetX, offsetY)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text("${component.type} ${component.text}", color = Color.White)
        println("$offsetX, $offsetY")
    }
}