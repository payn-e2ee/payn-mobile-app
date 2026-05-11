package com.example.payn.chat.domain

import com.example.payn.core.domain.models.Attachment

data class MessageDelivery(
    val id: String,
    val messageId: String,
    val senderDeviceId: String,
    val senderUserId: String,
    val recipientDeviceId: String,
    val recipientUserId: String,
    val ciphertext: String,
    val authTag: String,
    val ephemeralPublicKey: String,
    val messageCounter: Int,
    val type: MessageType,
    val attachmentId: String? = null,
    val createdAt: String,

    var attachment: Attachment? = null,
)