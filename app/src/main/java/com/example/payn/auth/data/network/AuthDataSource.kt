package com.example.payn.auth.data.network

import com.example.payn.auth.data.dto.AuthResponseDTO
import com.example.payn.auth.data.dto.LoginFormDTO
import com.example.payn.core.data.safeCall
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val BASE_URL = "http://10.0.2.2:3000/api/auth"

class AuthDataSource(private val httpClient: HttpClient) {
    suspend fun login(loginForm: LoginFormDTO): Result<ApiResponse<AuthResponseDTO>, DataError.Remote> {
        return safeCall {
            httpClient.post(
                urlString = "$BASE_URL/login"
            ) {
                contentType(ContentType.Application.Json)
                setBody(loginForm)
            }
        }
    }
}