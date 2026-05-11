package com.example.payn.chat.data.dto

import com.example.payn.core.data.dto.AttachmentDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageDeliveryDTO(
    val id: String,

    @SerialName("message_id")
    val messageId: String,

    @SerialName("sender_device_id")
    val senderDeviceId: String,

    @SerialName("sender_user_id")
    val senderUserId: String,

    @SerialName("recipient_device_id")
    val recipientDeviceId: String,

    @SerialName("recipient_user_id")
    val recipientUserId: String,

    val ciphertext: String,

    @SerialName("auth_tag")
    val authTag: String,

    @SerialName("ephemeral_public_key")
    val ephemeralPublicKey: String,

    val type: MessageTypeDTO,

    @SerialName("attachment_id")
    val attachmentId: String? = null,

    @SerialName("message_counter")
    val messageCounter: Int,

    @SerialName("identity_key")
    val identityKey: String,

    @SerialName("created_at")
    val createdAt: String,

    val attachment: AttachmentDTO? = null,
)