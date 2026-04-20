package com.example.payn.chat.data.repository

import com.example.payn.chat.data.dto.ChatDTO
import com.example.payn.chat.data.network.ChatDataSource
import com.example.payn.chat.domain.Chat
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result

class ChatRepository(
    private val chatDataSource: ChatDataSource,
) {
    suspend fun listChats(): Result<ApiResponse<List<ChatDTO>>, DataError> {
        return chatDataSource.listChats()
    }

    suspend fun getChatById(chatId: String): Result<ApiResponse<ChatDTO>, DataError> {
        return chatDataSource.getChatById(chatId)
    }
}