package com.example.server.models

import kotlinx.serialization.Serializable

@Serializable
data class ComponentCommand(
    val action: String, // "CREATE" 또는 "DELETE"
    val type: String? = null, // "BUTTON", "TEXT" (CREATE 시 필요)
    val text: String? = null, // "새 버튼" (CREATE 시 필요)
    val id: String? = null    // (DELETE 시 필요)
)