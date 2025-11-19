package com.example.kotlin_openmission_8.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageUploadResponse(
    val imageUrl: String // 서버가 이 URL을 반환해야 합니다.
)