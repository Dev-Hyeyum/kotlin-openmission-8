package com.example.server.models

import kotlinx.serialization.Serializable

@Serializable
data class EventAction(
    val type: String,          // 실행할 기능의 이름 (예: "SHOW_TOAST")
    val message: String? = null, // 토스트에 띄울 텍스트
    val targetUrl: String? = null // 리다이렉션 URL
)