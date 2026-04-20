package com.example.payn.contact.data.dto

import com.example.payn.core.data.dto.UserDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactDTO(
    val id: String,

    @SerialName("user_id") val userId: String,

    val firstname: String,

    val lastname: String,

    @SerialName("contact_user_id")
    val contactUserId: String,

    @SerialName("created_at")
    val createdAt: String,

    val contactUser: UserDTO? = null
)
