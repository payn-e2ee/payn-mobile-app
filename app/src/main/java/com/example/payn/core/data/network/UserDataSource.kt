package com.example.payn.core.data.network

import com.example.payn.core.data.dto.UserDTO
import com.example.payn.core.data.safeCall
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get

private const val BASE_URL = "http://10.0.2.2:3000/api/users"

class UserDataSource(private val httpClient: HttpClient) {
    suspend fun getCurrentUser(): Result<ApiResponse<UserDTO>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/"
            )
        }
    }
}