package com.example.kotlin_openmission_8

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kotlin_openmission_8.components.ComponentButton
import com.example.kotlin_openmission_8.components.ComponentViewer
import com.example.kotlin_openmission_8.components.Header
import com.example.kotlin_openmission_8.model.Component
import com.example.kotlin_openmission_8.model.ComponentAction
import com.example.kotlin_openmission_8.model.ComponentType
import com.example.kotlin_openmission_8.model.Components
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope

class MainActivity : ComponentActivity() {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            MyApp(context, client)
        }
    }
}

@Composable
fun MyApp(context: Context, client: HttpClient) {

    val viewModel = Components(client)

    val components by viewModel.components.collectAsState()
    var selectComponent by remember{ mutableStateOf(Component(action = ComponentAction.Create, type = ComponentType.Text, text = "dummy")) }

    LaunchedEffect(Unit) {
        viewModel.getComponent()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp, bottom = 30.dp, start = 20.dp, end = 20.dp)
    ) {
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(
                "컴포넌트 생성기",
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(color = Color.Blue, shape = RoundedCornerShape(15.dp))
                    .border(2.dp, Color.Black, shape = RoundedCornerShape(15.dp))
            )
            Surface (
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 20.dp, start = 10.dp, end = 10.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(15.dp))
            ) {
                Text(
                    text = "최근 생성한 컴포넌트 \n ${selectComponent.id} : ${selectComponent.action.name} : ${selectComponent.type.name} : ${selectComponent.text}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }
            ComponentViewer(
                components = components,
                viewModel = viewModel
            )
            ComponentButton(
                context = context,
                viewModel = viewModel,
                changeComponent = { newComponent -> selectComponent = newComponent }
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    val context = LocalContext.current
    val client = HttpClient()

    MyApp(
        context = context,
        client = client
    )
}
