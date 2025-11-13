package com.example.kotlin_openmission_8.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.kotlin_openmission_8.model.Components


@Composable
fun SideBar(
    context: Context,
    viewModel: Components,
    modifier: Modifier,
    resetPosition: () -> Unit
) {
    var showButtonList by remember { mutableStateOf(true) }
    Column(
        modifier = modifier
            .fillMaxHeight()
    ) {
        Row (
            modifier = Modifier.fillMaxWidth()
        ) {
            // 접기 버튼
            IconButton(
                onClick = {
                    showButtonList = !showButtonList
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "접기 버튼"
                )
            }
            // 0,0으로 보드를 돌리는 함수
            IconButton(
                onClick = {
                    resetPosition()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "메인으로 돌아가는 버튼"
                )
            }
        }

        // 사이드바
        if (showButtonList) {
            ButtonList(
                context = context,
                viewModel = viewModel,
                modifier = Modifier.weight(0.25f)
            )
        }
    }
}