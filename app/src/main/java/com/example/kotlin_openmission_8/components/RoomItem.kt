package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.kotlin_openmission_8.BuildConfig

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
        val errorPainter = rememberVectorPainter(Icons.Default.Warning)
        val placeHolderPainter = rememberVectorPainter(Icons.Default.Refresh)

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("${BuildConfig.BASE_URL}/uploads/$roomId/thumbnail.png?t=${System.currentTimeMillis()}")
                .crossfade(true)
                .listener(
                    onStart = { println("이미지 로딩 시작: $roomId") },
                    onError = { _, result ->
                        println("❌ 이미지 로드 실패: ${result.throwable.message}")
                        result.throwable.printStackTrace()
                    },
                    onSuccess = { _, _ -> println("✅ 이미지 로드 성공: $roomId") }
                )
                .build(),
            contentDescription = "썸네일",
            contentScale = ContentScale.Crop,
            error = errorPainter,
            placeholder = placeHolderPainter,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
        )
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
                        onDeleteClick()
                        showDialog = false
                    }
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }
}