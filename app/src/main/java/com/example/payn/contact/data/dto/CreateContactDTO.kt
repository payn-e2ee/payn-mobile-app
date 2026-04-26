package com.example.payn.contact.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateContactDTO(
    @SerialName("phone_number")
    val phoneNumber: String,
    val firstname: String,
    val lastname: String
)
