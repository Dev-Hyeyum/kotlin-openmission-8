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
    val actionOptions = listOf("NONE", "SHOW_TOAST", "OPEN_LINK")

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
            if (selectedActionType != "NONE") {
                val labelText = if (selectedActionType == "SHOW_TOAST") "메시지 내용" else "이동할 URL"
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
                                targetId = null
                            )
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