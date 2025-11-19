package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.github.skydoves.colorpicker.compose.ColorPickerController

@Composable
fun ColorInputSection(
    label: String,
    currentColor: Color,
    controller: ColorPickerController,
    showPicker: Boolean,
    onToggle: () -> Unit,
    onColorChange: (Color) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
            .background(currentColor, shape = RoundedCornerShape(15.dp))
            .clickable {
                controller.selectByColor(color = currentColor, fromUser = false)
                onToggle()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (currentColor.luminance() > 0.5) Color.Black else Color.White
        )
    }

    if (showPicker) {
        ColorPicker(
            controller = controller,
            onColorChanged = onColorChange // 5. 색상 변경 시 외부 콜백 호출
        )
    }
}