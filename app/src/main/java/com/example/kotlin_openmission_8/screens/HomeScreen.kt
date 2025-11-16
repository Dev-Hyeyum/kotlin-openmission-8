package com.example.kotlin_openmission_8.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.kotlin_openmission_8.components.HomeScreenContent
import com.example.kotlin_openmission_8.model.Components

@Composable
fun HomeScreen(
    viewModel: Components,
    onNavigateToCanvas: (String) -> Unit
) {
    val roomList by viewModel.roomList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchRoomList()
    }

    HomeScreenContent(
        roomList = roomList,
        onCreateCanvas = { viewModel.createCanvas() },
        onDeleteCanvas = { roomId ->
            viewModel.deleteCanvas(roomId)
        },
        onNavigateToCanvas = onNavigateToCanvas
    )
}

