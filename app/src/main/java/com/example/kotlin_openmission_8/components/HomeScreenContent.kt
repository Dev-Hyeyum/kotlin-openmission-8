package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreenContent(
    roomList: List<String>,
    onCreateCanvas: () -> Unit,
    onDeleteCanvas: (String) -> Unit,
    onNavigateToCanvas: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("어울림", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text("내맘대로 웹을 디자인해봐요~", fontSize = 12.sp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.Black, shape = RoundedCornerShape(15.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onCreateCanvas,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "캔버스 추가 버튼"
                    )
                }
                IconButton(
                    onClick = { }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "캔버스 선택 삭제 버튼"
                    )
                }
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(roomList) { roomId ->
                    RoomItem(
                        roomId = roomId,
                        onClick = {
                            onNavigateToCanvas(roomId)
                        },
                        onDeleteClick = { onDeleteCanvas(roomId) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "홈 화면 미리보기")
@Composable
fun HomeScreenContentPreview() {
    // 1. 미리보기에 표시할 가짜 데이터 생성
    val dummyRoomList = listOf("123456", "ABCDEF", "Test_Room_1")

    // 2. 컴포저블 호출 (람다는 비워두거나 print만 함)
    HomeScreenContent(
        roomList = dummyRoomList,
        onCreateCanvas = { println("캔버스 생성 클릭됨") },
        onDeleteCanvas = { id -> println("$id 삭제 클릭됨") },
        onNavigateToCanvas = { id -> println("$id 로 이동") }
    )
}