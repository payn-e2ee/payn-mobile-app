package com.example.payn.core.data.dto


import com.example.payn.chat.data.dto.ChatMemberDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: String,
    val username: String,
    val firstname: String,
    val lastname: String,
    val devices: List<DeviceDTO> = emptyList(),
    val chatMembers: List<ChatMemberDTO> = emptyList(),

    @SerialName("phone_number")
    val phoneNumber: String,

    @SerialName("profile_image_id")
    val profileImageId: String? = null
)