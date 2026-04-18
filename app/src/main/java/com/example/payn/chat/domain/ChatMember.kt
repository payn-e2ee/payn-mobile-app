package com.example.payn.chat.domain

import com.example.payn.core.domain.models.User

data class ChatMember(
    val id: String,
    val user: User,
    val createdAt: String
)