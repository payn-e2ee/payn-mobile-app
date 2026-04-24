package com.example.payn.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpResponseDTO(
    @SerialName("verification_token")
    val verificationToken: String
)
