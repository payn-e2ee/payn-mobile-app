package com.example.payn.auth.data.repository

import com.example.payn.auth.data.dto.AuthResponseDTO
import com.example.payn.auth.data.dto.LoginFormDTO
import com.example.payn.auth.data.dto.RegisterFormDTO
import com.example.payn.auth.data.dto.RegisterResponseDTO
import com.example.payn.auth.data.dto.SendOtpFormDTO
import com.example.payn.auth.data.dto.VerifyOtpFormDTO
import com.example.payn.auth.data.dto.VerifyOtpResponseDTO
import com.example.payn.auth.data.network.AuthDataSource
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.MessageResponse
import com.example.payn.core.domain.Result

class AuthRepository(
    private val authDataSource: AuthDataSource,
) {
    suspend fun login(
        username: String,
        password: String,
        identityKey: String
    ): Result<ApiResponse<AuthResponseDTO>, DataError.Remote> {
        return authDataSource
            .login(
                LoginFormDTO(
                    username,
                    password,
                    identityKey
                )
            )
    }

    suspend fun sendOtp(
        phoneNumber: String,
    ): Result<MessageResponse, DataError.Remote> {
        return authDataSource
            .sendOtp(
                SendOtpFormDTO(
                    phoneNumber,
                    bypass_token = "secret_bypass_token"
                )
            )
    }

    suspend fun verifyOtp(
        phoneNumber: String,
        code: Int
    ): Result<VerifyOtpResponseDTO, DataError.Remote> {

        return authDataSource
            .verifyOtp(
                VerifyOtpFormDTO(
                    phone_number = phoneNumber,
                    otp = code,
                    bypass_token = "secret_bypass_token"
                )
            )
    }

    suspend fun register(
        registerFormDTO: RegisterFormDTO
    ): Result<RegisterResponseDTO, DataError.Remote> {
        return authDataSource.register(registerFormDTO)
    }
}