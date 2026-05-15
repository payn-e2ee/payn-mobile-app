package com.example.payn.chat.data.repository

import com.example.payn.chat.data.dto.ChatDTO
import com.example.payn.chat.data.dto.InitChatDTO
import com.example.payn.chat.data.dto.MessageDTO
import com.example.payn.chat.data.dto.UpdateMessagesBatchFormDTO
import com.example.payn.chat.data.network.ChatDataSource
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

    suspend fun listMessages(
        chatId: String,
        offset: Int = 0
    ): Result<ApiResponse<List<MessageDTO>>, DataError> {
        return chatDataSource.listMessages(chatId, offset)
    }

    suspend fun initChat(initChatDTO: InitChatDTO): Result<ApiResponse<ChatDTO>, DataError> {
        return chatDataSource.initChat(initChatDTO)
    }

    suspend fun updateMessagesBatch(
        chatId: String,
        updateMessagesBatchFormDTO: UpdateMessagesBatchFormDTO
    ): Result<ApiResponse<List<MessageDTO>>, DataError.Remote> {
        return chatDataSource.updateMessagesBatch(chatId, updateMessagesBatchFormDTO)
    }
}