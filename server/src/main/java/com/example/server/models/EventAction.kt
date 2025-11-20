package com.example.server.models

import kotlinx.serialization.Serializable

@Serializable
data class EventAction(
    val trigger: String = "OnClick",
    val targetId: String? = null,
    val type: String,
    val value: String
)