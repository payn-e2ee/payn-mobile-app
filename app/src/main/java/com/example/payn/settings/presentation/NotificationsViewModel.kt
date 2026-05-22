package com.example.payn.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.payn.core.data.KeyValueStorage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val keyValueStorage: KeyValueStorage
) : ViewModel() {

    val messageNotificationsEnabled: StateFlow<Boolean> = keyValueStorage
        .getBoolean("message_notifications_enabled", true)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notificationSoundEnabled: StateFlow<Boolean> = keyValueStorage
        .getBoolean("notification_sound_enabled", true)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val vibrationEnabled: StateFlow<Boolean> = keyValueStorage
        .getBoolean("vibration_enabled", true)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setMessageNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            keyValueStorage.putBoolean("message_notifications_enabled", enabled)
        }
    }

    fun setNotificationSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            keyValueStorage.putBoolean("notification_sound_enabled", enabled)
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            keyValueStorage.putBoolean("vibration_enabled", enabled)
        }
    }
}
