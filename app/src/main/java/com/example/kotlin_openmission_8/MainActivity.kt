package com.example.kotlin_openmission_8
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.kotlin_openmission_8.model.Components
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json

// 앱의 진입점
class MainActivity : ComponentActivity() {

    // Ktor 라이브러리를 사용하여 네트워크 통신 준비
    private val client = HttpClient(CIO) {
        install(WebSockets)
        install(ContentNegotiation) {
            json()
        }
        install(WebSockets)
    }
    // 안드로이드 액티비티를 초기화
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 앱 화면을 화면 가장자리까지 확장
        enableEdgeToEdge()
        // Jetpack Compose UI
        setContent {
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            val viewModel = Components(client)
            // 화면그리기를 MainScreen에 위임
            MainScreen(context, coroutineScope, viewModel)
        }
    }
    // 액티비티가 종료되면 close 호출
    override fun onDestroy() {
        super.onDestroy()
        client.close()
    }
}
