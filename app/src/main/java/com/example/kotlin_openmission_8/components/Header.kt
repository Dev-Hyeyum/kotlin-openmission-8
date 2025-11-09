package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_openmission_8.ui.theme.Kotlinopenmission8Theme

@Composable
fun Header(name: String, modifier: Modifier = Modifier) {
    Surface {
        Box (
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Hello $name!", fontSize = 30.sp, color = Color.Red
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    Kotlinopenmission8Theme {
        Header("앱 제목")
    }
}