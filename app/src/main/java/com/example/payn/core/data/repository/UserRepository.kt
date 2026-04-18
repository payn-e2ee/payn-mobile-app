package com.example.payn.core.data.repository

import com.example.payn.core.data.dto.UserDTO
import com.example.payn.core.data.network.UserDataSource
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result

class UserRepository(
    private val userDataSource: UserDataSource,
) {
    suspend fun getCurrentUser(): Result<ApiResponse<UserDTO>, DataError.Remote> {
        return userDataSource.getCurrentUser()
    }
}