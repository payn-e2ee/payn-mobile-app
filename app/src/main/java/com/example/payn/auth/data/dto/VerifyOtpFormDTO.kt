package com.example.payn.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpFormDTO(
    val phone_number: String,
    val otp: Int,
    val bypass_token: String
)