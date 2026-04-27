package com.example.payn.chat.presentation.chat_list

import com.example.payn.chat.domain.Chat

data class ChatListState(
    var searchQuery: String = "",
    var chats: List<Chat> = emptyList(),
)