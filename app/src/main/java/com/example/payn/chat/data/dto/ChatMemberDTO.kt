package com.example.payn.chat.data.dto

import com.example.payn.core.data.dto.UserDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMemberDTO(
    val id: String,
    val user: UserDTO,
    @SerialName("created_at")
    val createdAt: String
)