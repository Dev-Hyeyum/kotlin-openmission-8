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
import kotlin.collections.chunked

@Composable
fun CreateMenu(
    context: Context,
    viewModel: Components
) {
    val availableComponents = listOf(
        ButtonItem("버튼", ComponentType.Button),
        ButtonItem("사진", ComponentType.Image),
        ButtonItem("텍스트", ComponentType.Text),
        ButtonItem("드롭다운", ComponentType.Dropdown),
        ButtonItem("입력 창", ComponentType.InputField),
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
        modifier = Modifier
            .fillMaxHeight()
            .background(Color(0xFF4D4D4D))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
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
                            context = context,
                            viewModel = viewModel,
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
        Spacer(Modifier.weight(1f))
    }
}