package com.example.payn.core.data

import com.example.payn.core.data.mappers.toUser
import com.example.payn.core.data.repository.UserRepository
import com.example.payn.core.domain.models.User
import com.example.payn.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull

class AuthSessionManager(
    val userRepository: UserRepository,
    val keyValueStorage: KeyValueStorage
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    suspend fun initializeSession() {
        userRepository.getCurrentUser().onSuccess { response ->
            updateUser(response.data.toUser())
        }
    }

    private fun updateUser(user: User?) {
        _currentUser.value = user
    }

    fun getUser(): User? {
        return currentUser.value
    }

    suspend fun getAccessToken(): String? {
        return keyValueStorage.getString("access_token").firstOrNull()
    }

    fun isLoggedIn(): Boolean = _currentUser.value != null
}