package com.example.payn.core.data.network

import com.example.payn.core.config.AppConfig
import com.example.payn.core.data.dto.UpdateUserFormDTO
import com.example.payn.core.data.dto.UserDTO
import com.example.payn.core.data.safeCall
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.contentType

private const val BASE_URL = "${AppConfig.BASE_API_URL}/users"

class UserDataSource(private val httpClient: HttpClient) {
    suspend fun getCurrentUser(): Result<ApiResponse<UserDTO>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/"
            )
        }
    }

    suspend fun updateFcmToken(token: String): Result<ApiResponse<Unit>, DataError.Remote> {
        return safeCall {
            httpClient.patch(
                urlString = "$BASE_URL/fcm-token"
            ) {
                setBody(mapOf("fcm_token" to token))
            }
        }
    }

    suspend fun getUserById(userId: String): Result<ApiResponse<UserDTO>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/$userId"
            )
        }
    }

    suspend fun updateCurrentUser(updateUserFormDTO: UpdateUserFormDTO): Result<ApiResponse<UserDTO>, DataError.Remote> {
        return safeCall {
            httpClient.patch(
                urlString = "$BASE_URL/"
            ) {
                contentType(ContentType.Application.Json)
                setBody(updateUserFormDTO)
            }
        }
    }
}