package com.example.kotlin_openmission_8.screens

import android.content.Context
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kotlin_openmission_8.components.MainContentArea
import com.example.kotlin_openmission_8.components.SideBar
import com.example.kotlin_openmission_8.model.Components

@Composable
fun CanvasScreen(
    context: Context,
    viewModel: Components,
    boardId: String, // ✨ 2. NavHost로부터 boardId를 받도록 수정
    navController: NavHostController // ✨ 2. onNavigateBack 대신 NavController를 받음
) {
    // 디바이스의 회전 감지
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val scrollState by viewModel.canvasScrollState.collectAsState()
    val (canvasOffsetX, canvasOffsetY) = scrollState

    val isShowSideBar by viewModel.isSideBarExpanded.collectAsState()

    LaunchedEffect(boardId) {
        viewModel.loadBoard(boardId)
    }

    BackHandler {
        // 1. ViewModel에게 방을 나간다고 알림 (WebSocket 종료)
        viewModel.leaveRoom()
        // 2. Navigation을 통해 이전 화면(HomeScreen)으로 돌아감
        navController.popBackStack()
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        // 핸드폰이 가로일 경우
        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .weight(if (isShowSideBar) 0.70f else 0.95f)
                        .fillMaxSize()
                        .background(Color(0xFFF1F1F1))
                        .padding(16.dp), // 캔버스 주위의 여백
                    contentAlignment = Alignment.Center
                ) {
                    MainContentArea(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFFFFFF))
                            .border(width = 1.dp, color = Color.Gray),
                        viewModel = viewModel,
                        canvasOffsetX = canvasOffsetX,
                        canvasOffsetY = canvasOffsetY
                    )
                }
                SideBar(
                    context = context,
                    viewModel = viewModel,
                    modifier = if (isShowSideBar) {
                        Modifier.weight(0.3f)
                    } else {
                        Modifier.weight(0.05f)
                    },
                    isShowSideBar = isShowSideBar,
                    isLandscape = isLandscape,
                    onNavigateBack = {
                        viewModel.leaveRoom()
                        navController.popBackStack()
                    }
                )
            }
        }
        // 핸드폰이 세로일 경우
        else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .weight(if (isShowSideBar) 0.75f else 0.9f)
                        .fillMaxSize()
                        .background(Color(0xFFF1F1F1))
                        .padding(32.dp), // 캔버스 주위의 여백
                    contentAlignment = Alignment.Center
                ) {
                    MainContentArea(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFFFFFF))
                            .border(width = 1.dp, color = Color.Gray),
                        viewModel = viewModel,
                        canvasOffsetX = canvasOffsetX,
                        canvasOffsetY = canvasOffsetY
                    )
                }
                SideBar(
                    context = context,
                    viewModel = viewModel,
                    modifier = if (isShowSideBar) {
                        Modifier.weight(0.25f)
                    } else {
                        Modifier.weight(0.1f)
                    },
                    isShowSideBar = isShowSideBar,
                    isLandscape = isLandscape,
                    onNavigateBack = {
                        viewModel.leaveRoom()
                        navController.popBackStack()
                    }
                )
            }
        }

    }
}
