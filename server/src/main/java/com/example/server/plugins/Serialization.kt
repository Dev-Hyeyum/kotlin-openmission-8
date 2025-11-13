package com.example.server.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            isLenient = true
            // 앱이 모르는 필드를 보내도 무시
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }
}