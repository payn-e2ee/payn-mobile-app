package com.example.payn.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDTO(
    @SerialName("access_token")
    val accessToken: String,
    val user: UserDTO
)