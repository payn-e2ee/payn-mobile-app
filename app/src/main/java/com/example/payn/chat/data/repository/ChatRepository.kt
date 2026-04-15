package com.example.payn.chat.data.repository

import com.example.payn.chat.data.network.ChatDataSource
import com.example.payn.chat.domain.Chat
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result

class ChatRepository(
    private val chatDataSource: ChatDataSource,
) {
    suspend fun listChats(): Result<List<Chat>, DataError> {
        return chatDataSource.listChats()
    }
}