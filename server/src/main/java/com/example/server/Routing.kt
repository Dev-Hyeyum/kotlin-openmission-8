package com.example.server

import com.example.server.model.Component
import com.example.server.model.ComponentRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        // 기존 GET 요청
        get("/components") {
            val components = ComponentRepository.getAll()
            // (참고: mapOf("hello" to components) 대신 리스트를 바로 보내는 것이 더 일반적입니다.)
            call.respond(components)
        }

        // [핵심] POST로 JSON을 받아 컴포넌트 추가
        post("/components") {
            try {
                // 1. 클라이언트가 보낸 JSON을 Component 객체로 자동 변환 (수신)
                val newComponent = call.receive<Component>()

                // 2. Repository에 추가
                ComponentRepository.addComponents(newComponent)

                // 3. 성공 응답 (생성됨)
                call.respond(HttpStatusCode.Created, "Component added successfully")

            } catch (e: Exception) {
                // 4. 실패 시 (예: JSON 형식이 안 맞을 때)
                call.respond(HttpStatusCode.BadRequest, "Invalid component format")
            }
        }

    }
}