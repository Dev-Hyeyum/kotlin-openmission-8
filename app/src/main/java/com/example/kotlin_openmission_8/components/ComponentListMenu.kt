package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kotlin_openmission_8.model.Components

@Composable
fun ComponentListMenu(
    viewModel: Components
) {
    val components by viewModel.components.collectAsState()

    Column(
        modifier = Modifier.fillMaxHeight().background(Color(0xFFFAFAFA)).verticalScroll(rememberScrollState())
    ) {
        components.forEach { component ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(15.dp))
                    .clickable {
                        viewModel.getComponent(component.id)
                        viewModel.isEditMenu()
                    }
            ) {
                Text(
                    text = "${component.type} : ${component.text} : ${component.layer}",
                    modifier = Modifier
                        .padding(5.dp)
                        .align(alignment = Alignment.Center))
            }
        }

    }

}