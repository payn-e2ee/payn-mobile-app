package com.example.payn.chat.data.network

import com.example.payn.chat.data.dto.ChatDTO
import com.example.payn.chat.data.dto.InitChatDTO
import com.example.payn.chat.data.dto.MessageDTO
import com.example.payn.chat.data.dto.UpdateMessagesBatchFormDTO
import com.example.payn.core.config.AppConfig
import com.example.payn.core.data.safeCall
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val BASE_URL = "${AppConfig.BASE_API_URL}/chats"

class ChatDataSource(private val httpClient: HttpClient) {
    suspend fun listChats(): Result<ApiResponse<List<ChatDTO>>, DataError.Remote> {
        return safeCall<ApiResponse<List<ChatDTO>>> {
            httpClient.get(
                urlString = "$BASE_URL/"
            )
        }
    }

    suspend fun getChatById(chatId: String): Result<ApiResponse<ChatDTO>, DataError.Remote> {
        return safeCall<ApiResponse<ChatDTO>> {
            httpClient.get(
                urlString = "$BASE_URL/$chatId"
            )
        }
    }

    suspend fun listMessages(
        chatId: String,
        offset: Int = 0
    ): Result<ApiResponse<List<MessageDTO>>, DataError.Remote> {
        return safeCall<ApiResponse<List<MessageDTO>>> {
            httpClient.get(
                urlString = "$BASE_URL/$chatId/messages?offset=$offset"
            )
        }
    }

    suspend fun initChat(initChatDTO: InitChatDTO): Result<ApiResponse<ChatDTO>, DataError> {
        return safeCall {
            httpClient.post(
                urlString = "${BASE_URL}/init"
            ) {
                contentType(ContentType.Application.Json)
                setBody(initChatDTO)
            }
        }
    }

    suspend fun updateMessagesBatch(
        chatId: String,
        updateMessagesBatchFormDTO: UpdateMessagesBatchFormDTO
    ): Result<ApiResponse<List<MessageDTO>>, DataError.Remote> {
        return safeCall<ApiResponse<List<MessageDTO>>> {
            httpClient.patch(
                urlString = "$BASE_URL/$chatId/messages/batch"
            ) {
                contentType(ContentType.Application.Json)
                setBody(updateMessagesBatchFormDTO)
            }
        }
    }
}
