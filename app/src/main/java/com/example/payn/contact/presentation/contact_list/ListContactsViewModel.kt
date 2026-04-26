package com.example.payn.contact.presentation.contact_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.payn.contact.data.mappers.toContact
import com.example.payn.contact.data.repository.ContactRepository
import com.example.payn.core.domain.onError
import com.example.payn.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ListContactsViewModel(
    private val contactRepository: ContactRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _state = MutableStateFlow(ListContactsState())
    val state = _state
        .onStart {
            fetchContactsList()
            observeSearchQuery()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private fun observeSearchQuery() {
        _searchQuery
            .debounce(500L)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isBlank()) {
                    fetchContactsList()
                } else {
                    searchContacts(query)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun searchContacts(query: String) {
        viewModelScope.launch {
            contactRepository.searchContacts(query)
                .onSuccess { response ->
                    _state.update {
                        it.copy(
                            contacts = response.data.map { contact -> contact.toContact() }
                        )
                    }
                }
        }
    }

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

    fun toggleAddContactModal(isOpen: Boolean) {
        _state.update {
            it.copy(
                isAddContactModalOpen = isOpen,
                newContactFirstName = "",
                newContactLastName = "",
                newContactPhoneNumber = "",
                addContactError = null
            )
        }
    }

    fun onFirstNameChange(value: String) {
        _state.update { it.copy(newContactFirstName = value) }
    }

    fun onLastNameChange(value: String) {
        _state.update { it.copy(newContactLastName = value) }
    }

    fun onPhoneNumberChange(value: String) {
        _state.update { it.copy(newContactPhoneNumber = value) }
    }

    fun addContact() {
        val currentState = _state.value
        if (currentState.newContactPhoneNumber.isBlank()) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isAddingContact = true, addContactError = null) }
            val createContactDTO = com.example.payn.contact.data.dto.CreateContactDTO(
                phoneNumber = currentState.newContactPhoneNumber,
                firstname = currentState.newContactFirstName,
                lastname = currentState.newContactLastName
            )

            contactRepository.createContact(createContactDTO)
                .onSuccess {
                    fetchContactsList()
                    toggleAddContactModal(false)
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            isAddingContact = false,
                            addContactError = "Failed to add contact" // You can map the error more specifically if needed
                        )
                    }
                }
        }
    }

    fun setSearchQuery(value: String) {
        _state.update { it.copy(searchQuery = value) }
        _searchQuery.value = value
    }
}