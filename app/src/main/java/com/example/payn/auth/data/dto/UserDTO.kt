package com.example.payn.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    private val id: String,
    private val username: String,
    private val firstname: String,
    private val lastname: String,
    @SerialName("phone_number")
    private val phoneNumber: String,
)