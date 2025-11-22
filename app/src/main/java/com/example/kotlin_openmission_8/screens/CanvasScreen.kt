package com.example.kotlin_openmission_8.screens

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kotlin_openmission_8.components.MainContentArea
import com.example.kotlin_openmission_8.components.SideBar
import com.example.kotlin_openmission_8.controller.SystemBarsHider
import com.example.kotlin_openmission_8.controller.captureView
import com.example.kotlin_openmission_8.model.Components
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeApi::class)
@Composable
fun CanvasScreen(
    context: Context,
    viewModel: Components,
    boardId: String,
    navController: NavHostController
) {
    // 디바이스의 회전 감지
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val scrollState by viewModel.canvasScrollState.collectAsState()
    val (canvasOffsetX, canvasOffsetY) = scrollState

    val isShowSideBar by viewModel.isSideBarExpanded.collectAsState()

    val scope = rememberCoroutineScope()

    val view = LocalView.current
    val window = (view.context as? Activity)?.window

    SystemBarsHider()

    val captureAndExit = {
        scope.launch {
            try {
                if (window != null) {
                    // ✨ [핵심 변경] 라이브러리 대신 PixelCopy 사용
                    // 현재 보고 있는 화면 전체를 캡처합니다.
                    val bitmap = captureView(view, window)

                    // 서버로 업로드 (기존 코드 동일)
                    viewModel.uploadThumbnail(boardId, bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                viewModel.leaveRoom()
                navController.popBackStack()
                viewModel.fetchRoomList()
            }
        }
    }

    LaunchedEffect(boardId) {
        viewModel.loadBoard(boardId)
    }

    BackHandler {
        captureAndExit()
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
                    isLandscape = true,
                    onNavigateBack = {
                        captureAndExit()
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
                        .weight(if (isShowSideBar) 0.75f else 0.95f)
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
                        Modifier.weight(0.05f)
                    },
                    isShowSideBar = isShowSideBar,
                    isLandscape = false,
                    onNavigateBack = {
                        captureAndExit()
                    }
                )
            }
        }
    }
}
