package com.example.payn.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatDTO(
    val id: String,
    val chatMembers: List<ChatMemberDTO>,
    val messages: List<MessageDTO>? = null,
    @SerialName("created_at")
    val createdAt: String
)