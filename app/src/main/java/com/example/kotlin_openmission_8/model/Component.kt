package com.example.kotlin_openmission_8.model

import kotlinx.serialization.Serializable

enum class ComponentType {
    Button, Text
}

enum class ComponentAction {
    Create, Delete
}

@Serializable
data class Component(
    private val action: ComponentAction,
    private val type: ComponentType,
    private val text: String
)
