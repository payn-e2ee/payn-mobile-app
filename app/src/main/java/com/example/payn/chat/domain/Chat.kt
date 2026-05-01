package com.example.payn.chat.domain

data class Chat(
    val id: String,
    val chatMembers: List<ChatMember>?,
    val messages: List<Message>?,
    val createdAt: String
)

