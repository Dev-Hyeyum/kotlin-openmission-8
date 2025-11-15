package com.example.kotlin_openmission_8.model

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
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

    val style: ComponentStyle = ComponentStyle(
        fontColor = "#FF000000",
        fontFamily = "Default",
        fontSize = 12.0f,
        fontWeight = "Normal",
        backgroundColor = "#FFFFFFFF"
    )
)
