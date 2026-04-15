package com.example.payn.chat.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val savedStateHandle: SavedStateHandle
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
                .onSuccess { description ->
                    _state.update {
                        it.copy(
                            id = it.id,
                            name = it.name
                        )
                    }
                }
        }
    }
}