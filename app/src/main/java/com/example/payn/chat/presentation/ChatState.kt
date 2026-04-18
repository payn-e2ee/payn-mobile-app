package com.example.payn.chat.presentation

import com.example.payn.chat.domain.Chat

data class ChatState(
    var searchQuery: String = "",
    var chats: List<Chat> = emptyList(),
)