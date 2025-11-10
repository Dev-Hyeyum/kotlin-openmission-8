package com.example.kotlin_openmission_8

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kotlin_openmission_8.components.CreateButton
import com.example.kotlin_openmission_8.components.Header
import com.example.kotlin_openmission_8.components.SideBarButton
import com.example.kotlin_openmission_8.model.ComponentType
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
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current

            MainScreen(context, coroutineScope, client)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        client.close()
    }
}

@Composable
fun MyApp(context: Context, coroutineScope: CoroutineScope, client: HttpClient) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(
                "어쩌구",
                Modifier.fillMaxWidth().height(50.dp)
            )
            CreateButton(coroutineScope, context, client, "텍스트")
            CreateButton(coroutineScope, context, client, "버튼")
        }

    }
}

@Composable
fun MainScreen(context: Context, coroutineScope: CoroutineScope, client: HttpClient) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            MainContentArea(
                modifier = Modifier.weight(0.75f)
            )

            SideBar(
                coroutineScope,
                context,
                client,
                modifier = Modifier.weight(0.25f)
            )
        }
    }
}

@Composable
fun MainContentArea(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFFE0E0E0))
            .border(width = 1.dp, color = Color.Black)
    ) {

        SimpleBlock(text = "버튼", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun SimpleBlock(text: String, modifier: Modifier = Modifier) {
    Button(
        onClick = { /* 편집 기능 */ },
        modifier = modifier.size(80.dp, 40.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6CB6B0))
    ) {
        Text(text = text, color = Color.White)
    }
}

@Composable
fun SideBar(
    coroutineScope: CoroutineScope,
    context: Context,
    client: HttpClient,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFF616161))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally, // 가운데 정렬
            verticalArrangement = Arrangement.spacedBy(16.dp) // 세로 간격 조정
        ) {
            SideBarButton(coroutineScope, context, client, "버튼", ComponentType.Button)
            SideBarButton(coroutineScope, context, client, "사진", ComponentType.Image)
            SideBarButton(coroutineScope, context, client, "텍스트", ComponentType.Text)
            SideBarButton(coroutineScope, context, client, "버튼", ComponentType.Button) // 재활용
            SideBarButton(coroutineScope, context, client, "드롭다운", ComponentType.Dropdown)
        }
    }
}

@Composable
fun ComponentRow(
    coroutineScope: CoroutineScope,
    context: Context,
    client: HttpClient,
    label1: String, type1: ComponentType,
    label2: String, type2: ComponentType
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 버튼 1
        SideBarButton(
            coroutineScope,
            context,
            client,
            label = label1,
            componentType = type1,
            modifier = Modifier.weight(1f)
        )
        // 버튼 2
        SideBarButton(
            coroutineScope,
            context,
            client,
            label = label2,
            componentType = type2,
            modifier = Modifier.weight(1f)
        )
    }
}