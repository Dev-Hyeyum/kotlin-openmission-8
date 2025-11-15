package com.example.kotlin_openmission_8.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kotlin_openmission_8.model.Components
@Composable
fun HomeScreen(
    viewModel: Components,
    onNavigateToDetail: () -> Unit
) {
    //ViewModel의 roomId를 관찰하도록
    val roomId by viewModel.currentRoomId.collectAsState()
    LaunchedEffect(roomId) {
        if (roomId != null) {
            onNavigateToDetail()
        }
    }

    // 'Stateless' Composable에 '상태'와 '이벤트'를 전달
    HomeScreenContent(
        roomId = roomId,
        onCreateCanvas = { viewModel.createCanvas() },
        onDeleteCanvas = { /* TODO: 캔버스 삭제 */ },
        onNavigateToDetail = onNavigateToDetail
    )
}
@Composable
fun HomeScreenContent(
    roomId: String?,
    onCreateCanvas: () -> Unit,
    onDeleteCanvas: () -> Unit,
    onNavigateToDetail: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("서비스 이름이 들어가면 좋을 듯(title)")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.Black),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onCreateCanvas,
                    enabled = (roomId == null)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "캔버스 추가 버튼"
                    )
                }
                IconButton(
                    onClick = onDeleteCanvas // ⭐️ '파라미터'로 받은 람다 사용
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "캔버스 삭제 버튼"
                    )
                }
            }
            Button(onClick = onNavigateToDetail) {
                Text("디테일 화면으로 가기")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(
        roomId = null, //
        onCreateCanvas = {},
        onDeleteCanvas = {},
        onNavigateToDetail = {}
    )
}