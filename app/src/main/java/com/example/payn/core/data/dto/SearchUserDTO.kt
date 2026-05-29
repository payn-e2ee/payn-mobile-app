package com.example.payn.core.data.dto

import com.example.payn.chat.data.dto.ChatMemberDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchUserDTO(
    val id: String,
    val firstname: String? = null,
    val lastname: String? = null,
    val username: String,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("has_chat")
    val hasChat: Boolean = false,
    @SerialName("is_contact")
    val isContact: Boolean = false,
    val chatMembers: List<ChatMemberDTO> = emptyList(),
)
