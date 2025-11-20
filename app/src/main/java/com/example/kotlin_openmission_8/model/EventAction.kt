package com.example.kotlin_openmission_8.model

import kotlinx.serialization.Serializable

@Serializable
data class EventAction(
    val trigger: String = "OnClick", // 이벤트 발생 조건
    val targetId: String? = null,    // 이벤트의 대상(타겟)
    val type: String,                // 어떤 이벤트인지
    val value: String                // 이벤트에 사용될 value
)