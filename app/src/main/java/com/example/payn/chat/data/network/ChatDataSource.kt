package com.example.payn.chat.data.network

import com.example.payn.chat.data.dto.ChatDTO
import com.example.payn.core.data.safeCall
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get

private const val BASE_URL = "http://10.0.2.2:3000/api/chats"

class ChatDataSource(private val httpClient: HttpClient) {
    suspend fun listChats(): Result<ApiResponse<List<ChatDTO>>, DataError.Remote> {
        return safeCall<ApiResponse<List<ChatDTO>>> {
            httpClient.get(
                urlString = "$BASE_URL/"
            )
        }
    }
}
