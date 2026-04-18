package com.example.payn.chat.domain

data class MessageDelivery(
    val id: String,
    val messageId: String,
    val deviceId: String,
    val userId: String,
    val ciphertext: String,
    val authTag: String,
    val ephemeralPublicKey: String,
    val type: String,
    val attachmentId: String,
    val createdAt: String
)