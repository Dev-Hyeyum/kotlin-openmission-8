package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.SurfaceCoroutineScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kotlin_openmission_8.model.Component
import com.example.kotlin_openmission_8.model.Components
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ComponentViewer(
    components: List<Component>,
    viewModel: Components
) {
    Surface (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
            .heightIn(max = 400.dp, min = 50.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(15.dp))
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "서버 컴포넌트 목록",
                modifier = Modifier
                    .border(2.dp, Color.Black, RoundedCornerShape(15.dp))
                    .background(Color.Green, RoundedCornerShape(15.dp))
                    .padding(top = 10.dp, bottom = 10.dp, start = 30.dp, end = 30.dp),
                textAlign = TextAlign.Center
            )
            LazyColumn(
                modifier = Modifier.padding(10.dp) // 내부 패딩 추가
            ) {
                items(components.size) { index ->
                    val component = components[index]
                    ItemText(component = component, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ItemText(
    component: Component,
    viewModel: Components
) {
    Surface (
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(15.dp))
            .background(Color.White, RoundedCornerShape(15.dp))
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            Text (
                text = "${component.id} : ${component.action.name} : ${component.type.name} : ${component.text}",
                modifier = Modifier.padding(10.dp),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = {
                    viewModel.deleteComponent(component.id)
                }
            ) {
                Text(text = "delete")
            }
        }
    }
}
