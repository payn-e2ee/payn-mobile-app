package com.example.payn.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InitChatDTO(
    @SerialName("message_frames")
    val messageFrames: List<MessageFrameDTO>
)