package com.example.kotlin_openmission_8.model

import kotlinx.serialization.Serializable
import java.util.UUID

enum class ComponentType {
    Button, Text
}

enum class ComponentAction {
    Create, Delete
}

@Serializable
data class Component(
    val id: String = UUID.randomUUID().toString(),
    val action: ComponentAction,
    val type: ComponentType,
    val text: String
)
