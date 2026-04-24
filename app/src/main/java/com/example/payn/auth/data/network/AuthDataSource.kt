package com.example.payn.auth.data.network

import com.example.payn.auth.data.dto.AuthResponseDTO
import com.example.payn.auth.data.dto.LoginFormDTO
import com.example.payn.auth.data.dto.RegisterFormDTO
import com.example.payn.auth.data.dto.RegisterResponseDTO
import com.example.payn.auth.data.dto.SendOtpFormDTO
import com.example.payn.auth.data.dto.VerifyOtpFormDTO
import com.example.payn.auth.data.dto.VerifyOtpResponseDTO
import com.example.payn.core.config.AppConfig
import com.example.payn.core.data.safeCall
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.MessageResponse
import com.example.payn.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val BASE_URL = "${AppConfig.BASE_API_URL}/auth"

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

    suspend fun sendOtp(sendOtpForm: SendOtpFormDTO): Result<MessageResponse, DataError.Remote> {
        return safeCall {
            httpClient.post(
                urlString = "$BASE_URL/send-otp"
            ) {
                contentType(ContentType.Application.Json)
                setBody(sendOtpForm)
            }
        }
    }

    suspend fun verifyOtp(verifyOtpFormDTO: VerifyOtpFormDTO): Result<VerifyOtpResponseDTO, DataError.Remote> {
        return safeCall {
            httpClient.post(
                urlString = "$BASE_URL/verify-otp"
            ) {
                contentType(ContentType.Application.Json)
                setBody(verifyOtpFormDTO)
            }
        }
    }

    suspend fun register(registerFormDTO: RegisterFormDTO): Result<RegisterResponseDTO, DataError.Remote> {
        return safeCall {
            httpClient.post(
                urlString = "$BASE_URL/register"
            ) {
                contentType(ContentType.Application.Json)
                setBody(registerFormDTO)
            }
        }
    }
}