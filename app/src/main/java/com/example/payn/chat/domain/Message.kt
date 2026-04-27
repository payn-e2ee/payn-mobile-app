package com.example.payn.chat.domain

data class Message(
    val id: String,
    val chatId: String,
    val userId: String,
    val deviceId: String,
    val status: String,
    val createdAt: String,
    var messageDeliveries: List<MessageDelivery>
)