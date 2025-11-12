package com.example.kotlin_openmission_8.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kotlin_openmission_8.model.ButtonItem
import com.example.kotlin_openmission_8.model.ComponentType
import com.example.kotlin_openmission_8.model.Components
import kotlinx.coroutines.CoroutineScope
import kotlin.collections.chunked

@Composable
fun ButtonList(
    coroutineScope: CoroutineScope,
    context: Context,
    viewModel: Components,
    modifier: Modifier = Modifier
) {
    val availableComponents = listOf(
        ButtonItem("버튼", ComponentType.Button),
        ButtonItem("사진", ComponentType.Image),
        ButtonItem("텍스트", ComponentType.Text),
        ButtonItem("드롭다운", ComponentType.Dropdown),
        ButtonItem("항목 5", ComponentType.Dummy),
        ButtonItem("항목 6", ComponentType.Dummy),
        ButtonItem("항목 7", ComponentType.Dummy),
        ButtonItem("항목 8", ComponentType.Dummy),
        ButtonItem("항목 9", ComponentType.Dummy),
        ButtonItem("항목 10", ComponentType.Dummy),
        ButtonItem("항목 11", ComponentType.Dummy),
        ButtonItem("항목 12", ComponentType.Dummy),
        ButtonItem("항목 13", ComponentType.Dummy),
    )

    // 버튼 리스트를 행의 개수가 2개인 리스트로 나눔
    val rowGroups = remember { availableComponents.chunked(2) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFF4D4D4D))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 버튼 목록
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
                            viewModel,
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