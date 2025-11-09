// server/src/main/java/com/example/server/models/StateComponent.kt

package com.example.server.models

import kotlinx.serialization.Serializable

@Serializable
data class StateComponent(
    val id: String,
    val type: ComponentType, // Component.kt의 Enum을 공유
    val text: String? = null
)