package com.example.payn.contact.data.network

import com.example.payn.chat.data.dto.ChatDTO
import com.example.payn.contact.data.dto.ContactDTO
import com.example.payn.core.config.AppConfig
import com.example.payn.core.data.safeCall
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get

private const val BASE_URL = "${AppConfig.BASE_API_URL}/contacts"

class ContactDataSource(private val httpClient: HttpClient) {
    suspend fun listContacts(): Result<ApiResponse<List<ContactDTO>>, DataError.Remote> {
        return safeCall<ApiResponse<List<ContactDTO>>> {
            httpClient.get(
                urlString = "$BASE_URL/"
            )
        }
    }
}
