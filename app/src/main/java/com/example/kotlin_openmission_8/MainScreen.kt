package com.example.kotlin_openmission_8

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_openmission_8.components.SideBarButton
import com.example.kotlin_openmission_8.model.ComponentType
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope

data class ComponentItem(
    val label: String,
    val type: ComponentType
)

val availableComponents = listOf(
    ComponentItem("버튼", ComponentType.Button),
    ComponentItem("사진", ComponentType.Image),
    ComponentItem("텍스트", ComponentType.Text),
    ComponentItem("드롭다운", ComponentType.Dropdown),
    ComponentItem("항목 5", ComponentType.Button),
    ComponentItem("항목 6", ComponentType.Image),
    ComponentItem("항목 7", ComponentType.Text),
    ComponentItem("항목 8", ComponentType.Button),
    ComponentItem("항목 9", ComponentType.Image),
    ComponentItem("항목 10", ComponentType.Text),
    ComponentItem("항목 11", ComponentType.Button),
    ComponentItem("항목 12", ComponentType.Image),
    ComponentItem("항목 13", ComponentType.Text),
)


@Composable
fun MainScreen(context: Context, coroutineScope: CoroutineScope, client: HttpClient) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxHeight()
                    .background(Color(0xFFF1F1F1))
                    .padding(32.dp), // 캔버스 주위의 여백
                contentAlignment = Alignment.Center
            ) {
                MainContentArea(modifier = Modifier.fillMaxSize())
            }

            // 사이드바
            SideBar(
                coroutineScope,
                context,
                client,
                modifier = Modifier.weight(0.25f)
            )
        }
    }
}

@Composable
fun MainContentArea(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFFFFFFFF))
            .border(width = 1.dp, color = Color.Gray)
    ) {
        // 스크롤 상태를 기억하는 변수 생성
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text("웹 페이지가 이곳에 표시됩니다.", color = Color.Gray)

            // 위한 임시 콘텐츠
            Spacer(modifier = Modifier.height(50.dp))
            Text("스크롤 테스트용 긴 내용 1", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(200.dp))
            Text("중간 내용 2", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(300.dp))
            Text("하단 내용 3", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(400.dp))
            Text("바닥 내용입니다. 4", fontSize = 20.sp)
            // --- 여기까지 테스트용 콘텐츠 ---
        }
    }
}

@Composable
fun SideBar(
    coroutineScope: CoroutineScope,
    context: Context,
    client: HttpClient,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFF4D4D4D))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Collapse Sidebar",
            tint = Color.White,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.Start)
        )
        val rowGroups = remember { availableComponents.chunked(2) }
        LazyColumn(
            // 이 LazyColumn이 버튼들을 감싸는 "박스"입니다.
            // fillMaxHeight()가 없으므로 내용물 크기만큼만 차지합니다.
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(rowGroups) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { item ->
                        SideBarButton(
                            coroutineScope,
                            context,
                            client,
                            label = item.label,
                            componentType = item.type,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        // 이 Spacer가 LazyColumn을 위로 밀어 올리고 하단의 빈 공간을 만듭니다.
        Spacer(Modifier.weight(1f))
    }
}
