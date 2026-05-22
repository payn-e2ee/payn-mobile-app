package com.example.payn.auth.data.dto

import com.example.payn.core.data.dto.UserDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDTO(
    @SerialName("access_token")
    val accessToken: String,
    val user: UserDTO
)