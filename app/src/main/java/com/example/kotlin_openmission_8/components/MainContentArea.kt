package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.kotlin_openmission_8.model.Components

@Composable
fun MainContentArea(modifier: Modifier, viewModel: Components) {
    // 컴포넌트 관리 리스트
    val componetsList by viewModel.components.collectAsState()

    // Box의 실제 크기를 저장할 상태
    var boxWidth by remember { mutableStateOf(0) }
    var boxHeight by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                val size = coordinates.size
                boxWidth = size.width
                boxHeight = size.height
            }
    ) {
        componetsList.forEach { component ->
            ComponentBox(
                component = component,
                parentWidth = boxWidth,
                parentHeight = boxHeight,
                onPositionChange = { x, y ->
                    viewModel.updateComponentPosition(component.id, x, y)
                },
                onSizeChange = { w, h ->
                    viewModel.updateComponentSize(component.id, w, h)
                }

            )
        }
    }
}