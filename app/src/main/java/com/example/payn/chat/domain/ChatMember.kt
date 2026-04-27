package com.example.payn.chat.domain

import com.example.payn.core.domain.models.User

data class ChatMember(
    val id: String,
    val userId: String,
    val user: User? = null,
    val chatId: String,
    val createdAt: String
)