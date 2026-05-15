package com.example.payn.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserFormDTO(
    val username: String? = null,

    val firstname: String? = null,

    val lastname: String? = null,

    val profile_image_id: String? = null,
)
