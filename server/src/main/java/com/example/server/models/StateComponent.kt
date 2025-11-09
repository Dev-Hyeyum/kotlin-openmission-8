
package com.example.server.models

import kotlinx.serialization.Serializable

@Serializable
data class StateComponent(
    val id: String,
    val type: ComponentType,
    val text: String? = null
)