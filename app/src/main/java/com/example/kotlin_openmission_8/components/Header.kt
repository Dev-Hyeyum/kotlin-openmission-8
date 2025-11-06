package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Header(name: String, modifier: Modifier = Modifier) {
    Surface {
        Box (
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Hello $name!"
            )
        }
    }

}