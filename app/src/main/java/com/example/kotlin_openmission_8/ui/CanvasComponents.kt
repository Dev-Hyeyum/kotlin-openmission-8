package com.example.kotlin_openmission_8.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SimpleBlock(text: String, modifier: Modifier = Modifier) {
    Button(
        onClick = { /* 편집 기능 */ },
        modifier = modifier.size(80.dp, 40.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4))
    ) {
        Text(text = text, color = Color.White)
    }
}