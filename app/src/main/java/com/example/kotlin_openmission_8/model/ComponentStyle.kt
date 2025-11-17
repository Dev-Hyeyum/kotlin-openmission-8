package com.example.kotlin_openmission_8.model

import kotlinx.serialization.Serializable

@Serializable
data class ComponentStyle(
    // 2. 모든 타입을 String, Float 등 원시 타입으로 변경
    var fontFamily: String = "Default", // Default, Serif, Monospace
    var fontWeight: String = "Normal", // Normal, Bold, Medium, Light,
    var fontSize: Float = 20.0f,
    var fontColor: String = "#FF000000",
    var backgroundColor: String = "#FFFFFFFF",
    var borderColor: String = "#FF000000"
)