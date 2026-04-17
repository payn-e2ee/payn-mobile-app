package com.example.payn.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginFormDTO(
    private val username: String,
    private val password: String,
    private val base64_identity_key: String
)