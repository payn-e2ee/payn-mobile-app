package com.example.payn.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageDeliveryDTO(
    val id: String,

    @SerialName("message_id")
    val messageId: String,

    @SerialName("device_id")
    val deviceId: String,

    @SerialName("user_id")
    val userId: String,

    val ciphertext: String,

    @SerialName("auth_tag")
    val authTag: String,

    @SerialName("ephemeral_public_key")
    val ephemeralPublicKey: String,

    val type: String,

    @SerialName("attachment_id")
    val attachmentId: String,

    @SerialName("created_at")
    val createdAt: String
)