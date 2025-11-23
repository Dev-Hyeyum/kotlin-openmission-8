package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun IncreaseDecreaseBox(value: Float, onChangeValue: (Float) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(0.9f).border(width = 1.dp, color = Color(0xFF79747e), shape = RoundedCornerShape(10f)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (value > 0f) {
                    onChangeValue(value - 1)
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "layer 내리는 버튼"
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "layer")
            Text(text = "${value.toInt()}")
        }
        IconButton(
            onClick = {
                onChangeValue(value + 1)
            }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "layer 올리는 버튼"
            )
        }
    }
}