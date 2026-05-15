package com.example.payn.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class MessageDTO(
    val id: String,

    @SerialName("chat_id")
    val chatId: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("device_id")
    val deviceId: String,

    val status: MessageStatusDTO,

    @SerialName("created_at")
    val createdAt: String,

    val messageDeliveries: List<MessageDeliveryDTO>
)