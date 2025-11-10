package com.example.kotlin_openmission_8.model

import kotlinx.serialization.Serializable

enum class ComponentType {

    // 콘텐츠
    Text, Image, Link, Button,

    // 입력
    InputField, TextArea, Dropdown, Checkbox, RadioButton, DatePicker, FileUpload,

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
