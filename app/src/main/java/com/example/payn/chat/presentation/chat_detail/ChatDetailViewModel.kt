package com.example.payn.chat.presentation.chat_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.payn.app.Route
import com.example.payn.chat.data.dto.InitChatDTO
import com.example.payn.chat.data.mappers.toChat
import com.example.payn.chat.data.mappers.toMessage
import com.example.payn.chat.data.repository.ChatRepository
import com.example.payn.chat.data.security.DoubleRatchetEngine
import com.example.payn.chat.data.service.ChatService
import com.example.payn.chat.domain.ChatMessage
import com.example.payn.core.data.AuthSessionManager
import com.example.payn.core.data.mappers.toUser
import com.example.payn.core.data.repository.UserRepository
import com.example.payn.core.domain.onSuccess
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


class ChatDetailViewModel(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val chatService: ChatService,
    private val doubleRatchetEngine: DoubleRatchetEngine,
    authSessionManager: AuthSessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val chatId = savedStateHandle.toRoute<Route.Chat>().id
    val userId = savedStateHandle.toRoute<Route.Chat>().userId
    val currentUser = authSessionManager.getUser()

    private val _state = MutableStateFlow(ChatDetailState())
    val state = _state
        .onStart {
            if (chatId != null) {
                initChat()
            } else {
                fetchUserById()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private suspend fun initChat() {
        if (chatId != null) {
            fetchChatById().join()
            fetchMessages().join()
            chatService.initializeChat(
                onReady = {
                    chatService.subscribeToChat(
                        chatId = chatId,
                        callback = { message ->
                            appendMessage(message)
                        }
                    )
                }
            )
        }
    }

    private fun fetchChatById(): Job {
        return viewModelScope.launch {
            chatRepository.getChatById(chatId!!)
                .onSuccess { response ->
                    val chat = response.data.toChat()
                    _state.update {
                        it.copy(
                            chat = chat,
                            user = chat.chatMembers?.first { chatMember ->
                                chatMember.user!!.id != currentUser?.id
                            }?.user
                        )
                    }
                }
        }
    }

    fun fetchMessages(): Job {
        return viewModelScope.launch {
            chatRepository.listMessages(chatId!!, _state.value.messages.size)
                .onSuccess { response ->
                    val messages = response.data.map { it.toMessage() }
                    _state.update {
                        it.copy(
                            messages = it.messages + messages.map { message ->
                                val messageDelivery = message.messageDeliveries.first()
                                ChatMessage(
                                    id = message.id,
                                    content = decryptMessage(
                                        ciphertext = messageDelivery.ciphertext,
                                        ephemeralPublicKey = messageDelivery.ephemeralPublicKey,
                                        messageCounter = messageDelivery.messageCounter,
                                        userId = messageDelivery.senderUserId,
                                        deviceId = messageDelivery.senderDeviceId
                                    ),
                                    userId = messageDelivery.senderUserId,
                                    createdAt = message.createdAt
                                )
                            }
                        )
                    }
                }
        }
    }

    private fun fetchUserById(): Job {
        return viewModelScope.launch {
            userRepository
                .getUserById(userId!!)
                .onSuccess { response ->
                    val user = response.data.toUser()
                    _state.update {
                        it.copy(
                            user = user
                        )
                    }
                }
        }
    }

    fun appendMessage(message: ChatMessage) {
        _state.update {
            it.copy(
                messages = listOf(message) + it.messages
            )
        }
    }

    suspend fun sendMessage(onCreateChat: (chatId: String) -> Unit) {
        if (chatId == null) {
            // Create Chat
            val messageFrames = chatService.getMessageFramesForInitChat(
                content = _state.value.message,
                user = _state.value.user!!,
                currentUser = currentUser!!,
            )
            chatRepository.initChat(
                InitChatDTO(
                    messageFrames = messageFrames
                )
            ).onSuccess { response ->
                onCreateChat(response.data.toChat().id)
            }
        } else {
            chatService.sendMessage(
                chatId = chatId,
                content = _state.value.message,
                user = _state.value.user!!,
                currentUser = currentUser!!,
            )
        }

        appendMessage(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                content = _state.value.message,
                userId = currentUser.id,
                createdAt = System.currentTimeMillis().toString()
            )
        )
        setMessage("")
    }

    fun setMessage(content: String) {
        _state.update { it.copy(message = content) }
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

    override fun onCleared() {
        super.onCleared()
        chatService.destroyChat()
    }
}