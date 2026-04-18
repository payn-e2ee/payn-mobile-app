package com.example.payn.auth.presentation.welcome

import androidx.lifecycle.ViewModel
import com.example.payn.core.data.repository.UserRepository
import kotlinx.coroutines.runBlocking

class WelcomeViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {
    fun isAuthenticated(): Boolean {
        return runBlocking {
            return@runBlocking when (userRepository.getCurrentUser()) {
                is com.example.payn.core.domain.Result.Error<*> -> false
                is com.example.payn.core.domain.Result.Success<*> -> true
            }
        }
    }
}