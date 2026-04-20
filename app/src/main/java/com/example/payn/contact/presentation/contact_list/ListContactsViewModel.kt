package com.example.payn.contact.presentation.contact_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.payn.contact.data.mappers.toContact
import com.example.payn.contact.data.repository.ContactRepository
import com.example.payn.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListContactsViewModel(
    private val contactRepository: ContactRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ListContactsState())
    val state = _state
        .onStart {
            fetchContactsList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private fun fetchContactsList() {
        viewModelScope.launch {
            contactRepository.listContacts()
                .onSuccess { response ->
                    _state.update {
                        it.copy(
                            contacts = response.data.map { contact -> contact.toContact() }
                        )
                    }
                }
        }
    }

    fun setSearchQuery(value: String) {
        _state.update { it.copy(searchQuery = value) }
    }
}