package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RoomItem(roomId: String, onClick: () -> Unit, onDeleteClick: () -> Unit) {

    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
            .border(1.dp, Color.Gray),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "캔버스 ID: $roomId")
        IconButton(onClick = {
                showDialog = true
            }
        ) {
            Icon(Icons.Default.Delete, contentDescription = "삭제")
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                // 팝업 바깥을 클릭해도 닫히도록
                showDialog = false
            },
            title = {
                Text(text = "삭제 확인")
            },
            text = {
                Text(text = "캔버스 '$roomId'를(을) 정말 삭제하시겠습니까?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick() // ✅ "삭제" 람다 실행
                        showDialog = false  // 팝업 닫기
                    }
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false // 팝업 닫기
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }
}