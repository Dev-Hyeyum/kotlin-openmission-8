package com.example.kotlin_openmission_8

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kotlin_openmission_8.components.CreateButton
import com.example.kotlin_openmission_8.components.Header

@Composable
fun MainScreen(modifier: Modifier) {
    val context = LocalContext.current
    Surface (
        modifier = modifier
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(
                "어쩌구",
                Modifier.fillMaxWidth().height(50.dp)
            )
            CreateButton(context, "텍스트")
            CreateButton(context, "버튼")
        }

    }
}

