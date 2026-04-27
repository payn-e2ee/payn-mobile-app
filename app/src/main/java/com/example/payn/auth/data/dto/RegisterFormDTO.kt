package com.example.payn.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterFormDTO(
    private val username: String,
    private val phone_number: String,
    private val password: String,
    private val firstname: String,
    private val lastname: String,
    private val verification_token: String
)