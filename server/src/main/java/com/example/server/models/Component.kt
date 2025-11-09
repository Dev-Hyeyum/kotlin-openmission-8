package com.example.server.models

import kotlinx.serialization.Serializable

enum class ComponentType {
    Button, Text
}

enum class ComponentAction {
    Create, Delete
}

@Serializable
data class Component(
    val action: ComponentAction,
    val type: ComponentType,
    val text: String? = null,
    val id: String? = null
)