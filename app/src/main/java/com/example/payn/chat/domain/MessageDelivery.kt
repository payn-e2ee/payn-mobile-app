package com.example.payn.chat.domain

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
    val type: String,
    val attachmentId: String,
    val createdAt: String
)