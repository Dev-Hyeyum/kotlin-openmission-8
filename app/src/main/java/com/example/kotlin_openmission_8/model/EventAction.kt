package com.example.kotlin_openmission_8.model

import kotlinx.serialization.Serializable

@Serializable
data class EventAction(
    val type: String,           // ✨ 실행할 기능의 이름 (예: "SHOW_TOAST", "REDIRECT")
    val message: String? = null, // 기능의 매개변수 1 (예: 토스트에 띄울 텍스트)
    val targetUrl: String? = null // 기능의 매개변수 2 (예: 이동할 URL)
)