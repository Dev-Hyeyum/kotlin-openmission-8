package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(
    options: List<String>,             // 드롭다운에 표시할 선택지 목록
    label: String,                     // 필드의 라벨 텍스트
    currentSelection: String,          // 현재 선택된 값 (UI 표시용)
    onSelectionChange: (String) -> Unit, // 값이 변경되었을 때 호출할 콜백
    modifier: Modifier = Modifier
) {
    // 1. ✨ 내부적으로 확장 상태만 관리
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor().fillMaxWidth(0.9f), // 너비 조정
            readOnly = true,
            value = currentSelection, // 2. 외부에서 받은 현재 선택값 표시
            onValueChange = {},
            label = { Text(label) }, // 3. 외부에서 받은 라벨 표시
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onSelectionChange(selectionOption) // 4. 외부 콜백 호출
                        expanded = false // 5. 메뉴 닫기
                    }
                )
            }
        }
    }
}