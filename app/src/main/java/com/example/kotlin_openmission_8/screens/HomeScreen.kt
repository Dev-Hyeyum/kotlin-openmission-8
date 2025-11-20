package com.example.kotlin_openmission_8.screens

import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.kotlin_openmission_8.components.HomeScreenContent
import com.example.kotlin_openmission_8.model.Components
import com.example.kotlin_openmission_8.model.ComponentsViewModelFactory
import com.example.kotlin_openmission_8.ui.theme.Kotlinopenmission8Theme
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlin.getValue

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