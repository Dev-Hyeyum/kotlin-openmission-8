package com.example.kotlin_openmission_8.model

import kotlinx.serialization.Serializable
import java.util.UUID

enum class ComponentType {

    // 콘텐츠
    Text, Image, Link, Button,

    // 입력
    InputField, TextArea, Dropdown, Checkbox, RadioButton, DatePicker, FileUpload,

    // 더미
    Dummy

}

enum class ComponentAction {
    Create, Delete, Update
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

    val style: Map<String, String>? = null
)
