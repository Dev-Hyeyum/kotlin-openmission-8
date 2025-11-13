package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.example.kotlin_openmission_8.model.Components

@Composable
fun MainContentArea(
    modifier: Modifier,
    viewModel: Components,
    canvasOffsetX: Float,
    canvasOffsetY: Float,
    canvasPosition: (Float, Float) -> Unit
) {
    // 컴포넌트 관리 리스트
    val componetsList by viewModel.components.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds() // 영역 밖으로 나간 컴포넌트 안 보이게 자르기
            // 2. 배경(빈 공간)을 드래그하면 캔버스 전체 이동
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, _, _ ->
                    canvasPosition(pan.x, pan.y)
                }
            }
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = canvasOffsetX
                    translationY = canvasOffsetY
                }
        ) {
            componetsList.forEach { component ->
                ComponentBox(
                    component = component,
                    viewModel = viewModel
                )
            }
        }

    }
}