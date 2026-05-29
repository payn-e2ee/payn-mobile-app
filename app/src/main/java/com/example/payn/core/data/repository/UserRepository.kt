package com.example.payn.core.data.repository

import com.example.payn.core.data.dto.SearchUserDTO
import com.example.payn.core.data.dto.UpdateUserFormDTO
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

    suspend fun getUserById(userId: String): Result<ApiResponse<UserDTO>, DataError.Remote> {
        return userDataSource.getUserById(userId)
    }

    suspend fun searchUsers(query: String, limit: Int = 20): Result<ApiResponse<List<SearchUserDTO>>, DataError.Remote> {
        return userDataSource.searchUsers(query, limit)
    }

    suspend fun updateFcmToken(token: String): Result<ApiResponse<Unit>, DataError.Remote> {
        return userDataSource.updateFcmToken(token)
    }

    suspend fun updateCurrentUser(
        updateUserFormDTO: UpdateUserFormDTO,
        profileImageBytes: ByteArray? = null
    ): Result<ApiResponse<UserDTO>, DataError.Remote> {
        return userDataSource.updateCurrentUser(updateUserFormDTO, profileImageBytes)
    }
}