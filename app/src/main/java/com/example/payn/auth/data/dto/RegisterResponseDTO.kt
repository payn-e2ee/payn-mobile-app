package com.example.payn.auth.data.dto

import com.example.payn.core.data.dto.UserDTO
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDTO(
    val message: String,
    val user: UserDTO
)
