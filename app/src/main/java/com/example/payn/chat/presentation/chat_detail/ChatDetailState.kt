package com.example.payn.chat.presentation.chat_detail

import android.net.Uri
import com.example.payn.chat.domain.Chat
import com.example.payn.chat.domain.ChatMessage
import com.example.payn.core.domain.models.User

data class ChatDetailState(
    var chat: Chat? = null,
    var user: User? = null,
    var messages: List<ChatMessage> = emptyList(),
    var message: String = "",

    var selectedImageUri: Uri? = null,
    var showSelectedImagePopup: Boolean = false,

    var selectedVideoUri: Uri? = null,
    var showSelectedVideoPopup: Boolean = false,

    var isFullScreenOpen: Boolean = false,
)