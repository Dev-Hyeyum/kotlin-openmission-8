package com.example.kotlin_openmission_8

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_openmission_8.components.MainContentArea
import com.example.kotlin_openmission_8.components.SideBar
import com.example.kotlin_openmission_8.components.SideBarButton
import com.example.kotlin_openmission_8.model.ComponentType
import com.example.kotlin_openmission_8.model.Components
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope

@Composable
fun MainScreen(context: Context, coroutineScope: CoroutineScope, viewModel: Components) {
    // 디바이스의 회전 감지
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // 웹소켓 연결
    LaunchedEffect(Unit) {
        viewModel.connectWebSocket()
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
                        .weight(0.75f)
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
                        viewModel = viewModel
                    )
                }
                SideBar(
                    coroutineScope = coroutineScope,
                    context = context,
                    viewModel = viewModel,
                    modifier = Modifier.weight(0.25f)
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
                        .weight(0.75f)
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
                        viewModel = viewModel
                    )
                }
                SideBar(
                    coroutineScope = coroutineScope,
                    context = context,
                    viewModel = viewModel,
                    modifier = Modifier.weight(0.25f)
                )
            }
        }

    }
}
