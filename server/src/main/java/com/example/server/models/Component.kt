package com.example.server.models

import kotlinx.serialization.Serializable

// '명령'의 종류 (Enum)
enum class ComponentAction {
    Create, Delete, Update
}

// '명령'의 타입 (Enum)
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
    val text: String? = null,
    val id: String? = null
)