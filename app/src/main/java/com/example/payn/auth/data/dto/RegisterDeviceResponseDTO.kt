package com.example.payn.auth.data.dto

import com.example.payn.core.data.dto.DeviceDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceResponseDTO(
    @SerialName("access_token")
    val accessToken: String,
    val device: DeviceDTO
)