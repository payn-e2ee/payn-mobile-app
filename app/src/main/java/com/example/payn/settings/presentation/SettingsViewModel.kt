package com.example.payn.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.payn.core.data.KeyValueStorage
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val keyValueStorage: KeyValueStorage
) : ViewModel() {

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            keyValueStorage.remove("access_token")
            onSuccess()
        }
    }
}
