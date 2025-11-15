package com.example.kotlin_openmission_8.components

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.kotlin_openmission_8.model.Components

@Composable
fun MenuBar(
    imageVector: ImageVector,
    viewModel: Components,
    isShowFunction: () -> Unit,
    onNavigateBack: () -> Unit
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
            onNavigateBack()
        }
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "홈 화면으로 돌아가는 버튼"
        )
    }
}