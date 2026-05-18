package com.example.payn.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceFormDTO(
    @SerialName("access_token")
    private val accessToken: String,

    @SerialName("base64_identity_key")
    private val base64IdentityKey: String
)