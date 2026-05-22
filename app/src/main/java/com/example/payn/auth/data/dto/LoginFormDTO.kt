package com.example.payn.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginFormDTO(
    val username: String,
    val password: String,
)