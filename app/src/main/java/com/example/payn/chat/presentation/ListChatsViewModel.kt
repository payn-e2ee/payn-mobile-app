package com.example.payn.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.payn.chat.data.mappers.toChat
import com.example.payn.chat.data.repository.ChatRepository
import com.example.payn.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListChatsViewModel(
    private val chatRepository: ChatRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
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
                            chats = response.data?.map { chatDTO -> chatDTO.toChat() } ?: emptyList()
                        )
                    }
                }
        }
    }

    fun setSearchQuery(value: String) {
        _state.update { it.copy(searchQuery = value) }
    }
}