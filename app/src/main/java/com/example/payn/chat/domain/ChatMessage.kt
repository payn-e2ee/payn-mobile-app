package com.example.payn.chat.domain

data class ChatMessage(
    val id: String,
    val content: String,
    val userId: String,
    val createdAt: String,
)
