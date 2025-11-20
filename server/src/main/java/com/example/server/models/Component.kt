package com.example.server.models

import kotlinx.serialization.Serializable
import java.util.UUID

// '명령'의 종류 (Enum)
@Serializable
enum class ComponentAction {
    Create, Delete, Update
}

// '명령'의 타입 (Enum)
@Serializable
enum class ComponentType {

    // 콘텐츠
    Text, Image, Link, Button,

    // 입력
    InputField, TextArea, Dropdown, Checkbox, RadioButton, DatePicker, FileUpload,

    // 더미
    Dummy
}

@Serializable
data class Component(
    val action: ComponentAction,
    val type: ComponentType,
    val text: String ?= null,
    val id: String= UUID.randomUUID().toString(),
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    var width: Float = 200f,
    var height: Float = 150f,

    val style: ComponentStyle = ComponentStyle(
        fontColor = "#FF000000",
        fontFamily = "Default",
        fontSize = 20.0f,
        fontWeight = "Normal",
        backgroundColor = "#FFFFFFFF",
        borderColor = "#FF000000",
        borderRadius = 0.0f,
    ),
    val actions: List<EventAction> = emptyList(),
    val imageUrl: String? = null
)
