package com.example.payn.core.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: String,
    val username: String,
    val firstname: String,
    val lastname: String,

    @SerialName("phone_number")
    val phoneNumber: String
)