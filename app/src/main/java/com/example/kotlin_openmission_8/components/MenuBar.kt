package com.example.kotlin_openmission_8.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.kotlin_openmission_8.model.Components

@Composable
fun MenuBar(
    imageVector: ImageVector,
    viewModel: Components,
    isShowFunction: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var showShareDialog by remember { mutableStateOf(false) }
    val webUrl by viewModel.currentWebUrl.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth() // 너비를 꽉 채움
            .horizontalScroll(rememberScrollState()) // 가로 스크롤 가능하게 설정
    ) {
// 접기 버튼
        IconButton(
            onClick = {
                isShowFunction()
            }
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = "접기 버튼"
            )
        }
        // 컴포넌트를 생성하는 메뉴를 여는 버튼
        IconButton(
            onClick = {
                viewModel.isCreateMenu()
            }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "컴포넌트를 설정하는 메뉴 버튼"
            )
        }
        // 옵션 정하는 메뉴를 여는 버튼
        IconButton(
            onClick = {
                viewModel.isEditMenu()
            }
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "컴포넌트를 설정하는 메뉴 버튼"
            )
        }
        // 0,0으로 보드를 돌리는 함수
        IconButton(
            onClick = {
                viewModel.resetCanvas()
            }
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "스크린 좌표를 0,0 돌아가는 버튼"
            )
        }
        IconButton(
            onClick = {
                showShareDialog = true // 팝업 띄우기
            }
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "로컬호스트 링크 공유 버튼"
            )
        }
        IconButton(
            onClick = {
                viewModel.leaveRoom()
                onNavigateBack()
            }
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "홈 화면으로 돌아가는 버튼"
            )
        }
    }

    if (showShareDialog) {
        // 3. 안드로이드 시스템(브라우저)에 접근하기 위해 Context 가져오기
        val context = LocalContext.current
        // 4. URL 문자열이 null이 아닐 경우에만 Intent 생성
        val intent = remember(webUrl) {
            webUrl?.let { Intent(Intent.ACTION_VIEW, Uri.parse(it)) }
        }

        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text("캔버스 공유") },
            text = {
                Column {
                    Text("웹 브라우저에서 이 주소로 접속하세요:")
                    Spacer(Modifier.height(8.dp))

                    SelectionContainer {
                        Text(
                            text = webUrl ?: "URL을 생성하는 중입니다...",
                            fontWeight = FontWeight.Bold,
                            // 5. 링크처럼 보이도록 스타일 추가
                            color = if (intent != null) Color.Blue else Color.Gray,
                            textDecoration = if (intent != null) TextDecoration.Underline else null,
                            // 6. 클릭 이벤트 추가
                            modifier = if (intent != null) {
                                Modifier.clickable {
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // 브라우저가 없는 등 예외 처리
                                        Toast.makeText(context, "링크를 열 수 없습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Modifier // intent가 null이면 클릭 안 됨
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showShareDialog = false }) {
                    Text("닫기")
                }
            }
        )
    }
}