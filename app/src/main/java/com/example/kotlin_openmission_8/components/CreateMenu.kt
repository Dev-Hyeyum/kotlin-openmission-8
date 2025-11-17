package com.example.kotlin_openmission_8.components

import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_openmission_8.model.ComponentType
import com.example.kotlin_openmission_8.model.Components
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMenu(
    viewModel: Components
) {
    // 컴포넌트
    val component by viewModel.component.collectAsState()

    // component의 x,y 값 데이터를 불러옴
    var offsetX by remember { mutableFloatStateOf(component.offsetX) }
    var offsetY by remember { mutableFloatStateOf(component.offsetY) }
    // component의 높이와 너비 데이터를 불러옴
    var boxWidth by remember { mutableFloatStateOf(component.width) }
    var boxHeight by remember { mutableFloatStateOf(component.height) }
    // component의 텍스트 데이터를 불러옴
    var text by remember { mutableStateOf(component.text) }

    // style 데이터
    var fontSize by remember { mutableStateOf(component.style.fontSize) }
    var fontWeight by remember { mutableStateOf(component.style.fontWeight) }
    var fontColor by remember { mutableStateOf(Color(parseColor(component.style.fontColor))) }
    var backGroundColor by remember { mutableStateOf(Color(parseColor(component.style.backgroundColor))) }
    var fontFamily by remember { mutableStateOf(component.style.fontFamily) }
    var borderColor by remember { mutableStateOf(Color(parseColor(component.style.borderColor))) }

    // 색 조작하기 위한 컨트롤러
    val fontColorController = rememberColorPickerController()
    val backGroundColorController = rememberColorPickerController()
    var showFontColor by remember { mutableStateOf(false) }
    var showBackGroundColor by remember { mutableStateOf(false) }
    val borderColorController = rememberColorPickerController()
    var showBorderColor by remember { mutableStateOf(false) }
    var borderRadius by remember { mutableStateOf(component.style.borderRadius) }
    // 외부(서버/ViewModel)에서 데이터가 변경되면 내부 상태도 갱신
    // component 키값이 바뀌면(즉, 리스트 내용이 갱신되면) 이 블록이 실행
    LaunchedEffect(component) {
        offsetX = component.offsetX
        offsetY = component.offsetY
        boxWidth = component.width
        boxHeight = component.height
        text = component.text

        fontSize = component.style.fontSize
        fontWeight = component.style.fontWeight
        fontFamily = component.style.fontFamily
        try {
            fontColor = Color(parseColor(component.style.fontColor))
            backGroundColor = Color(parseColor(component.style.backgroundColor))
            borderColor = Color(parseColor(component.style.borderColor))
        } catch (e: Exception) { /* 기본값 유지 */ }
    }

    Column (
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 스타일 관련 편집 Field 추가하면 됨.
        OutlinedTextField(
            value = boxWidth.toString(),
            onValueChange = { boxWidth = it.toFloatOrNull() ?: boxWidth },
            label = { Text("Width") }
        )
        OutlinedTextField(
            value = boxHeight.toString(),
            onValueChange = { boxHeight = it.toFloatOrNull() ?: boxHeight },
            label = { Text("Height") }
        )
        OutlinedTextField(
            value = offsetX.toString(),
            onValueChange = { offsetX = it.toFloatOrNull() ?: offsetX },
            label = { Text("Offset X") }
        )
        OutlinedTextField(
            value = offsetY.toString(),
            onValueChange = { offsetY = it.toFloatOrNull() ?: offsetY },
            label = { Text("Offset Y") }
        )
        if (component.type == ComponentType.Text || component.type == ComponentType.Button) {
            OutlinedTextField(
                value = text.toString(),
                onValueChange = { text = it },
                label = { Text("Text") }
            )
        }
        val fontWeightOptions = listOf("Normal", "Bold", "Medium", "Light")
        var fontWeightExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = fontWeightExpanded,
            onExpandedChange = { fontWeightExpanded = !fontWeightExpanded },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor(), // 메뉴와 TextField를 연결
                readOnly = true,
                value = fontWeight, // 현재 선택된 값 (String)
                onValueChange = {},
                label = { Text("Font Weight") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fontWeightExpanded) },
            )
            ExposedDropdownMenu(
                expanded = fontWeightExpanded,
                onDismissRequest = { fontWeightExpanded = false }
            ) {
                fontWeightOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            fontWeight = selectionOption // String 상태 업데이트
                            fontWeightExpanded = false
                        }
                    )
                }
            }
        }

        // --- ✨ [추가] FontFamily 드롭다운 ---
        val fontFamilyOptions = listOf("Default", "Serif", "Monospace")
        var fontFamilyExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = fontFamilyExpanded,
            onExpandedChange = { fontFamilyExpanded = !fontFamilyExpanded },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = fontFamily, // 현재 선택된 값 (String)
                onValueChange = {},
                label = { Text("Font Family") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fontFamilyExpanded) },
            )
            ExposedDropdownMenu(
                expanded = fontFamilyExpanded,
                onDismissRequest = { fontFamilyExpanded = false }
            ) {
                fontFamilyOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            fontFamily = selectionOption // String 상태 업데이트
                            fontFamilyExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = fontSize.toString(),
            onValueChange = {
                fontSize = it.toFloatOrNull() ?: fontSize
            },
            label = { Text("Font Size") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = borderRadius.toString(),
            onValueChange = {
                borderRadius = it.toFloatOrNull() ?: borderRadius
            },
            label = { Text("Border Radius (px)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // 글씨 색
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                .background(fontColor, shape = RoundedCornerShape(15.dp))
                .clickable{
                    fontColorController.selectByColor(color = fontColor, fromUser = false)
                    showFontColor = !showFontColor
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "글씨 색상",
                color = if (backGroundColor.luminance() > 0.5) Color.Black else Color.White
            )
        }
        if (showFontColor) {
            ColorPicker(controller = fontColorController,
                onColorChanged = { newColor ->
                    fontColor = newColor
                }
            )
        }
        // 배경 색
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                .background(backGroundColor, shape = RoundedCornerShape(15.dp))
                .clickable{
                    backGroundColorController.selectByColor(color = backGroundColor, fromUser = false)
                    showBackGroundColor = !showBackGroundColor
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "배경 색상",
                color = if (backGroundColor.luminance() > 0.5) Color.Black else Color.White
            )
        }
        if (showBackGroundColor) {
            ColorPicker(controller = backGroundColorController,
                onColorChanged = { newColor ->
                    backGroundColor = newColor
                }
            )
        }
        // 경계선 색
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                .background(borderColor, shape = RoundedCornerShape(15.dp))
                .clickable{
                    borderColorController.selectByColor(color = borderColor, fromUser = false)
                    showBorderColor = !showBorderColor
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "선 색상",
                color = if (borderColor.luminance() > 0.5) Color.Black else Color.White // ⬅️
            )
        }
        if (showBorderColor) {
            ColorPicker(controller = borderColorController,
                onColorChanged = { newColor ->
                    borderColor = newColor
                }
            )
        }
        Row (
            modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp).fillMaxWidth(),
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
                    viewModel.updateComponent(
                        id = component.id,
                        offsetX = offsetX,
                        offsetY = offsetY,
                        width = boxWidth,
                        height = boxHeight,
                        text = text,
                        style = newStyle
                    )
                }
            ) {
                Text("확인")
            }
        }
    }
}
