package com.example.payn.chat.presentation.chat_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.payn.app.Route
import com.example.payn.chat.data.mappers.toChat
import com.example.payn.chat.data.repository.ChatRepository
import com.example.payn.core.domain.onError
import com.example.payn.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.map

class ChatDetailViewModel(
    private val chatRepository: ChatRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val chatId = savedStateHandle.toRoute<Route.Chat>().id
    private val userId = savedStateHandle.toRoute<Route.Chat>().userId

    private val _state = MutableStateFlow(ChatDetailState())
    val state = _state
        .onStart {
            if (chatId != null) {
                fetchChatById()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private fun fetchChatById() {
        if (chatId == null) {
            return
        }

        viewModelScope.launch {
            chatRepository.getChatById(chatId)
                .onSuccess { response ->
                    val chat = response.data.toChat()
                    _state.update {
                        it.copy(
                            chat = chat,
                            user = chat.chatMembers.first().user
                        )
                    }
                }
        }
    }
}