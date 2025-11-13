package com.example.kotlin_openmission_8.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.kotlin_openmission_8.model.Components
import kotlinx.coroutines.CoroutineScope


@Composable
fun SideBar(
    coroutineScope: CoroutineScope,
    context: Context,
    viewModel: Components,
    modifier: Modifier
) {
    var showButtonList by remember { mutableStateOf(true) }
    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
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
        // 사이드바
        if (showButtonList) {
            ButtonList(
                coroutineScope,
                context,
                viewModel,
                modifier = Modifier.weight(0.25f)
            )
        }
    }
}