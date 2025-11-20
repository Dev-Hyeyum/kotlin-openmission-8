package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kotlin_openmission_8.model.ComponentType
import com.example.kotlin_openmission_8.model.Components
import com.example.kotlin_openmission_8.model.EventAction
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMenu(
    viewModel: Components
) {
    // 컴포넌트
    val component by viewModel.component.collectAsState()

    val allComponents by viewModel.components.collectAsState()
    // component의 데이터
    var offsetX by remember { mutableFloatStateOf(component.offsetX) }
    var offsetY by remember { mutableFloatStateOf(component.offsetY) }
    var boxWidth by remember { mutableFloatStateOf(component.width) }
    var boxHeight by remember { mutableFloatStateOf(component.height) }
    var text by remember { mutableStateOf(component.text) }

    // style 데이터
    var fontSize by remember { mutableFloatStateOf(component.style.fontSize) }
    var fontWeight by remember { mutableStateOf(component.style.fontWeight) }
    var fontColor by remember { mutableStateOf(Color(component.style.fontColor.toColorInt())) }
    var backGroundColor by remember { mutableStateOf(Color(component.style.backgroundColor.toColorInt())) }
    var fontFamily by remember { mutableStateOf(component.style.fontFamily) }
    var borderColor by remember { mutableStateOf(Color(component.style.borderColor.toColorInt())) }
    var borderRadius by remember { mutableFloatStateOf(component.style.borderRadius) }

    // 이벤트(액션) 관련 상태
    var selectedActionType by remember { mutableStateOf("NONE") }
    var actionValue by remember { mutableStateOf("") }
    val actionOptions = listOf("NONE", "SHOW_TOAST", "OPEN_LINK", "SET_TEXT")
    var selectedTargetId by remember { mutableStateOf<String?>(null) } // 타겟 ID 저장

    // 색 조작하기 위한 컨트롤러
    val fontColorController = rememberColorPickerController()
    val backGroundColorController = rememberColorPickerController()
    val borderColorController = rememberColorPickerController()

    // 상태데이터
    var showBorderColor by remember { mutableStateOf(false) }
    var showFontColor by remember { mutableStateOf(false) }
    var showBackGroundColor by remember { mutableStateOf(false) }

    LaunchedEffect(component) {
        offsetX = component.offsetX
        offsetY = component.offsetY
        boxWidth = component.width
        boxHeight = component.height
        text = component.text
        fontSize = component.style.fontSize
        fontWeight = component.style.fontWeight
        fontFamily = component.style.fontFamily

        // 기존 액션 정보 불러오기
        val firstAction = component.actions.firstOrNull()
        if (firstAction != null) {
            selectedActionType = firstAction.type
            actionValue = firstAction.value
            selectedTargetId = firstAction.targetId
        } else {
            selectedActionType = "NONE"
            actionValue = ""
        }

        try {
            fontColor = Color(component.style.fontColor.toColorInt())
            backGroundColor = Color(component.style.backgroundColor.toColorInt())
            borderColor = Color(component.style.borderColor.toColorInt())
        } catch (e: Exception) { /* 기본값 유지 */ }
    }

    Column (
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NumberDataField(data = boxWidth, label = "Width", changeData = { boxWidth = it })
        NumberDataField(data = boxHeight, label = "Height", changeData = { boxHeight = it })
        NumberDataField(data = offsetX, label = "offsetX", changeData = { offsetX = it })
        NumberDataField(data = offsetY, label = "offsetY", changeData = { offsetY = it })

        if (component.type == ComponentType.Text || component.type == ComponentType.Button) {
            OutlinedTextField(
                value = text.toString(),
                modifier = Modifier.fillMaxWidth(0.9f),
                onValueChange = { text = it },
                label = { Text("Text") }
            )
        }
        val fontWeightOptions = listOf("Normal", "Bold", "Medium", "Light")

        DropDownMenu(options = fontWeightOptions,
            label = "Font Weight",
            currentSelection = fontWeight,
            onSelectionChange = { fontWeight = it },
            modifier = Modifier.padding(top = 8.dp)
        )

        val fontFamilyOptions = listOf("Default", "Serif", "Monospace")
        DropDownMenu(
            options = fontFamilyOptions,
            label = "Font Family",
            currentSelection = fontFamily,
            onSelectionChange = { fontFamily = it },
            modifier = Modifier.padding(top = 8.dp)
        )

        NumberDataField(data = fontSize, label = "Font Size", changeData = {data -> fontSize = data})
        NumberDataField(data = borderRadius, label = "Border Radius", changeData = {data -> borderRadius = data})

        ColorInputSection(
            label = "글씨 색상",
            currentColor = fontColor,
            controller = fontColorController,
            showPicker = showFontColor,
            onToggle = { showFontColor = !showFontColor },
            onColorChange = { fontColor = it }
        )

        ColorInputSection(
            label = "배경 색상",
            currentColor = backGroundColor,
            controller = backGroundColorController,
            showPicker = showBackGroundColor,
            onToggle = { showBackGroundColor = !showBackGroundColor },
            onColorChange = { backGroundColor = it }
        )

        ColorInputSection(
            label = "선 색상",
            currentColor = borderColor,
            controller = borderColorController,
            showPicker = showBorderColor,
            onToggle = { showBorderColor = !showBorderColor },
            onColorChange = { borderColor = it }
        )

        if (component.type == ComponentType.Button) {
            Text(
                text = "이벤트 설정",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )

            // 액션 타입 선택 (DropDownMenu 재사용)
            DropDownMenu(
                options = actionOptions,
                label = "클릭 시 동작",
                currentSelection = selectedActionType,
                onSelectionChange = { selectedActionType = it },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 액션 값 입력 (NONE이 아닐 때만 표시)
            if (selectedActionType == "SET_TEXT") {
                val otherComponents = allComponents.filter { it.id != component.id }
                val targetList = listOf(component) + otherComponents

                // 드롭다운에 표시할 이름
                val targetNames = targetList.map { item ->
                    if (item.id == component.id) {
                        "자신 (현재 선택된 컴포넌트)"
                    } else {
                        val content = item.text?.take(8) ?: "내용없음" // 텍스트 앞 8글자
                        val shortId = item.id.takeLast(4) // ID 뒤 4글자 (구분용)
                        "${item.type} : $content (#$shortId)"
                    }
                }

                // 3. 현재 선택된 타겟의 이름 찾기 (초기값 표시용)
                val currentTargetName = if (selectedTargetId == component.id) {
                    "자신 (현재 선택된 컴포넌트)"
                } else {
                    val found = targetList.find { it.id == selectedTargetId }
                    if (found != null) {
                        val content = found.text?.take(8) ?: "내용없음"
                        val shortId = found.id.takeLast(4)
                        "${found.type} : $content (#$shortId)"
                    } else {
                        "선택하세요"
                    }
                }

                DropDownMenu(
                    options = targetNames,
                    label = "대상 컴포넌트 (Target)",
                    currentSelection = currentTargetName,
                    onSelectionChange = { selectedName ->
                        // 선택된 이름(String)을 기반으로 실제 객체(Component)의 ID를 찾음
                        val index = targetNames.indexOf(selectedName)
                        if (index != -1) {
                            selectedTargetId = targetList[index].id
                        }
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // 값 입력
            if (selectedActionType != "NONE") {
                val labelText = when (selectedActionType) {
                    "SHOW_TOAST" -> "메시지 내용"
                    "OPEN_LINK" -> "이동할 URL"
                    "SET_TEXT" -> "변경할 텍스트 값"
                    else -> "값 입력"
                }
                OutlinedTextField(
                    value = actionValue,
                    modifier = Modifier.fillMaxWidth(0.9f),
                    onValueChange = { actionValue = it },
                    label = { Text(labelText) },
                    singleLine = true
                )
            }
        }

        Row (
            modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 20.dp, bottom = 20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = {
                    viewModel.deleteComponent(component.id)
                }
            ) {
                Text("컴포넌트 삭제")
            }
            TextButton(
                onClick = {
                    val newFontColorString = "#${fontColor.toArgb().toUInt().toString(16)}"
                    val newBackColorString = "#${backGroundColor.toArgb().toUInt().toString(16)}"
                    val newBorderColorString = "#${borderColor.toArgb().toUInt().toString(16)}"

                    val newStyle = component.style.copy(
                        fontWeight = fontWeight,
                        fontSize = fontSize,
                        fontFamily = fontFamily,
                        fontColor = newFontColorString,
                        backgroundColor = newBackColorString,
                        borderColor = newBorderColorString,
                        borderRadius = borderRadius,
                    )

                    // 선택된 이벤트 정보를 리스트로 구성
                    val newActions = if (selectedActionType != "NONE") {
                        listOf(
                            EventAction(
                                trigger = "OnClick",
                                type = selectedActionType,
                                value = actionValue,
                                targetId = if (selectedActionType == "SET_TEXT") selectedTargetId else null                            )
                        )
                    } else {
                        emptyList()
                    }

                    viewModel.updateComponent(
                        id = component.id,
                        offsetX = offsetX,
                        offsetY = offsetY,
                        width = boxWidth,
                        height = boxHeight,
                        text = text,
                        style = newStyle,
                        actions = newActions
                    )
                }
            ) {
                Text("확인")
            }
        }
    }
}