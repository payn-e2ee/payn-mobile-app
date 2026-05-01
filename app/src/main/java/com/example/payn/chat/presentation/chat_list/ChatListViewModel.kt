package com.example.payn.chat.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.payn.chat.data.mappers.toChat
import com.example.payn.chat.data.repository.ChatRepository
import com.example.payn.chat.data.security.DoubleRatchetEngine
import com.example.payn.core.data.AuthSessionManager
import com.example.payn.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatListViewModel(
    private val chatRepository: ChatRepository,
    private val doubleRatchetEngine: DoubleRatchetEngine,
    authSessionManager: AuthSessionManager
) : ViewModel() {

    val currentUser = authSessionManager.getUser()
    private val _state = MutableStateFlow(ChatListState())
    val state = _state
        .onStart {
            fetchChatsList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private fun fetchChatsList() {
        viewModelScope.launch {
            chatRepository
                .listChats()
                .onSuccess { response ->
                    _state.update {
                        it.copy(
                            chats = response.data.map { chatDTO -> chatDTO.toChat() }
                        )
                    }
                }
        }
    }

    fun setSearchQuery(value: String) {
        _state.update { it.copy(searchQuery = value) }
    }

    suspend fun decryptMessage(
        ciphertext: String,
        ephemeralPublicKey: String,
        messageCounter: Int,
        userId: String,
        deviceId: String
    ): String {
        val isFromMe = currentUser?.id == userId

        if (!isFromMe && doubleRatchetEngine.isFirstTimeSeeingEphemeralPublicKey(ephemeralPublicKey)) {
            return String(
                doubleRatchetEngine.decryptMessage(
                    ciphertext = ciphertext,
                    remoteEphemeralPublicKey = ephemeralPublicKey,
                    messageCounter = messageCounter,
                    remoteDeviceId = deviceId,
                )
            )
        }

        return String(
            doubleRatchetEngine.decryptStateless(
                ciphertext = ciphertext,
                ephemeralPublicKey = ephemeralPublicKey,
                messageCounter = messageCounter,
                isFromMe = isFromMe,
            )
        )
    }
}