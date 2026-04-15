package com.example.payn.chat.data.network

import com.example.payn.chat.domain.Chat
import com.example.payn.core.data.safeCall
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get

private const val BASE_URL = "http://192.168.100.102:3000"

class ChatDataSource(private val httpClient: HttpClient) {
    suspend fun listChats(): Result<List<Chat>, DataError.Remote> {
        return safeCall<List<Chat>> {
            httpClient.get(
                urlString = "$BASE_URL/api/chats"
            )
        }
    }
}
