package com.example.payn.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.payn.core.data.AuthSessionManager
import com.example.payn.core.data.KeyValueStorage
import com.example.payn.core.domain.models.User
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val keyValueStorage: KeyValueStorage,
    private val authSessionManager: AuthSessionManager
) : ViewModel() {

    val currentUser: User? = authSessionManager.getUser()
    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            keyValueStorage.remove("access_token")
            onSuccess()
        }
    }
}
