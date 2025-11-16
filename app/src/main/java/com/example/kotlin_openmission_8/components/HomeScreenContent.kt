package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreenContent(
    roomList: List<String>,
    onCreateCanvas: () -> Unit,
    onDeleteCanvas: (String) -> Unit,
    onNavigateToCanvas: (String) -> Unit
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
