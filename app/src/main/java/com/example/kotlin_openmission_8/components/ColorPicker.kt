package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker

@Composable
fun ColorPicker(controller: ColorPickerController, onColorChanged: (Color) -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(15.dp))
    ) {

        // 2. 메인 색상 선택기
        HsvColorPicker(
            modifier = Modifier
                .padding(all = 5.dp)
                .height(150.dp), // 크기 조절
            controller = controller,
            onColorChanged = { colorEnvelope ->
                // 5. 색상(ColorEnvelope)이 아닌 Color 객체만 부모에게 전달
                onColorChanged(colorEnvelope.color)
            }
        )

        // 3. 투명도 슬라이더
        AlphaSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 10.dp)
                .height(25.dp),
            controller = controller
        )

        // 4. 명도 슬라이더
        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 10.dp)
                .height(25.dp),
            controller = controller
        )

    }
}