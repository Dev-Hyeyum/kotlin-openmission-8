package com.example.server.models

import kotlinx.serialization.Serializable

// '명령'의 종류 (Enum)
enum class ComponentAction {
    Create, Delete, Update
}

// '명령'의 타입 (Enum)
enum class ComponentType {
    Button, Text
}
@Serializable
data class Component(
    val action: ComponentAction,
    val type: ComponentType,
    val text: String? = null,
    val id: String? = null
)