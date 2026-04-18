package com.example.payn.auth.data.repository

import com.example.payn.auth.data.dto.AuthResponseDTO
import com.example.payn.auth.data.dto.LoginFormDTO
import com.example.payn.auth.data.network.AuthDataSource
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
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
}