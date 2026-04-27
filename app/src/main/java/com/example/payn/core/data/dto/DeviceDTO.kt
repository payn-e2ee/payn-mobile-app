package com.example.payn.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceDTO(
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("identity_key")
    val identityKey: String,

    @SerialName("created_at")
    val createdAt: String
)