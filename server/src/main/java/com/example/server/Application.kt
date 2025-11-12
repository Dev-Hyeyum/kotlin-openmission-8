package com.example.server

import com.example.server.models.Component
import com.example.server.models.ComponentAction
import com.example.server.models.ComponentRepository
import com.example.server.plugins.configureRouting
import com.example.server.plugins.configureSerialization
import com.example.server.plugins.configureWebSocket
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // 업데이트를 테스트하는 비동기함수입니다.
    testUpdate()
    configureSerialization()
    configureRouting()
    configureWebSocket()
}

fun testUpdate() {
    // 테스트 코드입니다. 1초에 한번씩 업데이트를 진행합니다.
    GlobalScope.launch {
        var counter = 1
        val targetId = "text_1"

        while (true) {
            // 1초 마다
            delay(1000L)
            // 타겟을 지정해서 가져옴
            val currentState = ComponentRepository.getComponent(targetId)
            if (currentState != null) {
                //  Update '명령'을 생성
                val newText = "서버 업데이트: ${counter++}"
                // 텍스트 업데이트
                val updateCommand = Component(
                    action = ComponentAction.Update,
                    id = targetId,
                    type = currentState.type,
                    text = newText
                )
                RoomController.broadcastCommand(updateCommand)
                println("[Timer] $newText 로 업데이트 브로드캐스트")
            }
        }
    }
}
