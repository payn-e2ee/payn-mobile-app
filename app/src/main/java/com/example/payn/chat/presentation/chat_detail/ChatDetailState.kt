package com.example.payn.chat.presentation.chat_detail

import com.example.payn.chat.domain.Chat
import com.example.payn.core.domain.models.User

data class ChatDetailState(
    var chat: Chat? = null,
    var user: User? = null
)