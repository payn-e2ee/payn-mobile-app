package com.example.payn.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageHeaderDTO(
    @SerialName("chat_id")
    val chatId: String,

    @SerialName("sender_user_id")
    val senderUserId: String,

    @SerialName("sender_device_id")
    val senderDeviceId: String,

    @SerialName("recipient_user_id")
    val recipientUserId: String,

    @SerialName("recipient_device_id")
    val recipientDeviceId: String,

    @SerialName("message_id")
    val messageId: String,

    @SerialName("sender_identity_key")
    val senderIdentityKey: String,

    @SerialName("sender_ephemeral_public_key")
    val senderEphemeralPublicKey: String,

    @SerialName("message_counter")
    val messageCounter: Int
)