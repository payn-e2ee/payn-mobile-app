package com.example.payn.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SendOtpFormDTO(
    private val phone_number: String,
    private val bypass_token: String
)