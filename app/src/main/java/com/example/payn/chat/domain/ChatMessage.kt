package com.example.payn.chat.domain

import com.example.payn.core.domain.models.Attachment

data class ChatMessage(
    val id: String,
    val ciphertext: String,
    val ephemeralPublicKey: String,
    val messageCounter: Int,
    val userId: String,
    val deviceId: String,
    val attachment: Attachment?,
    val messageType: MessageType,
    val createdAt: String,
)
