package com.example.kotlin_openmission_8.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kotlin_openmission_8.components.SideBarButton // 임포트
import com.example.kotlin_openmission_8.model.ComponentType // 임포트
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues

data class ComponentItem(
    val label: String,
    val type: ComponentType
)

val availableComponents = listOf(
    ComponentItem("버튼", ComponentType.Button),
    ComponentItem("사진", ComponentType.Image),
    ComponentItem("텍스트", ComponentType.Text),
    ComponentItem("드롭다운", ComponentType.Dropdown),
)


@Composable
fun MainScreen(context: Context, coroutineScope: CoroutineScope, client: HttpClient) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 1. 메인 컨텐츠 영역 (에디터/캔버스)
            MainContentArea(
                modifier = Modifier.weight(0.75f)
            )

            // 2. 사이드바 영역 (컴포넌트 목록)
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
            .background(Color(0xFFE0E0E0))
            .border(width = 1.dp, color = Color.Black) // 분할선
    ) {
        SimpleBlock(text = "버튼", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun SideBar(
    coroutineScope: CoroutineScope,
    context: Context,
    client: HttpClient,
    modifier: Modifier = Modifier
) {
    val rowGroups = remember { availableComponents.chunked(2) }

    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFF616161))
            .padding(horizontal = 8.dp),

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
                        modifier = Modifier.weight(1f) // 버튼이 가로 공간을 정확히 1:1로 나눕니다.
                    )
                }

                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}