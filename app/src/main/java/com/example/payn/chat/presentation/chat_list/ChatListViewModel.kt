package com.example.payn.chat.presentation.chat_list

import android.util.Base64
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

    private val messagesCache = mutableMapOf<String, String>()
    suspend fun decryptMessage(
        messageId: String,
        ciphertext: String,
        ephemeralPublicKey: String,
        messageCounter: Int,
        senderDeviceId: String,
        receiptDeviceId: String
    ): String {
        var plaintext = messagesCache[messageId]
        if (plaintext != null) {
            return plaintext
        }

        val currentDeviceId = currentUser?.devices?.firstOrNull()?.id ?: return ""
        val isFromCurrentDevice = senderDeviceId == currentDeviceId
        plaintext =
            if (!isFromCurrentDevice && doubleRatchetEngine.isFirstTimeSeeingEphemeralPublicKey(
                    Base64.decode(ephemeralPublicKey, Base64.DEFAULT)
                )
            ) {
                String(
                    doubleRatchetEngine.decryptMessage(
                        ciphertext = Base64.decode(ciphertext, Base64.DEFAULT),
                        remoteEphemeralPublicKey = Base64.decode(
                            ephemeralPublicKey,
                            Base64.DEFAULT
                        ),
                        messageCounter = messageCounter,
                        remoteDeviceId = senderDeviceId,
                    )
                )
            } else {
                String(
                    doubleRatchetEngine.decryptStateless(
                        ciphertext = Base64.decode(ciphertext, Base64.DEFAULT),
                        ephemeralPublicKey = Base64.decode(ephemeralPublicKey, Base64.DEFAULT),
                        messageCounter = messageCounter,
                        isFromCurrentDevice = isFromCurrentDevice,
                        remoteDeviceId = if (isFromCurrentDevice) receiptDeviceId else senderDeviceId,
                    )
                )
            }
        messagesCache[messageId] = plaintext
        return plaintext
    }
}