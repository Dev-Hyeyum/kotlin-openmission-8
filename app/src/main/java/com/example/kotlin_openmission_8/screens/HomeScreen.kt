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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kotlin_openmission_8.model.Components

@Composable
fun HomeScreen(
    // viewModel을 매개변수로
    viewModel: Components,
    onNavigateToDetail: () -> Unit
) {
    //ViewModel의 roomId를 관찰하도록
    val roomId by viewModel.currentRoomId.collectAsState()
    LaunchedEffect(roomId) {
        if(roomId != null) {
            onNavigateToDetail()
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("서비스 이름이 들어가면 좋을 듯(title)")
            // 캔버스 생성 및 삭제 바
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.Black),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {

                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "캔버스 추가 버튼"
                    )
                }
                IconButton(
                    onClick = {

                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "캔버스 삭제 버튼"
                    )
                }
            }
            // 지금까지 만들어놓은 캔버스로 이동하는 함수
            Button(onClick = onNavigateToDetail) {
                Text("디테일 화면으로 가기")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(onNavigateToDetail = {})
}