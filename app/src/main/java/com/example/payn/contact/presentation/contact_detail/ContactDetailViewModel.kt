package com.example.payn.contact.presentation.contact_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.payn.app.Route
import com.example.payn.contact.data.mappers.toContact
import com.example.payn.contact.data.repository.ContactRepository
import com.example.payn.contact.data.dto.UpdateContactDTO
import com.example.payn.core.domain.onError
import com.example.payn.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactDetailViewModel(
    private val contactRepository: ContactRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val contactId = savedStateHandle.toRoute<Route.Contact>().id

    private val _state = MutableStateFlow(ContactDetailState())
    val state = _state
        .onStart {
            fetchContactById()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private fun fetchContactById() {
        viewModelScope.launch {
            contactRepository.getContactById(contactId)
                .onSuccess { response ->
                    val contact = response.data.toContact()
                    _state.update {
                        it.copy(
                            contact = contact
                        )
                    }
                }
        }
    }

    fun deleteContact(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, error = null) }
            contactRepository.deleteContact(contactId)
                .onSuccess {
                    _state.update { it.copy(isDeleting = false) }
                    onDeleted()
                }
                .onError { error ->
                    _state.update { 
                        it.copy(
                            isDeleting = false,
                            error = error::class.simpleName ?: "Unknown Error"
                        ) 
                    }
                }
        }
    }

    fun toggleEditModal(isOpen: Boolean) {
        _state.update { 
            it.copy(
                isEditModalOpen = isOpen,
                editFirstName = it.contact?.firstname ?: "",
                editLastName = it.contact?.lastname ?: "",
                updateError = null
            ) 
        }
    }

    fun onFirstNameChange(name: String) {
        _state.update { it.copy(editFirstName = name) }
    }

    fun onLastNameChange(name: String) {
        _state.update { it.copy(editLastName = name) }
    }

    fun updateContact() {
        viewModelScope.launch {
            val currentState = _state.value
            _state.update { it.copy(isUpdating = true, updateError = null) }
            
            val updateDTO = UpdateContactDTO(
                firstname = currentState.editFirstName,
                lastname = currentState.editLastName
            )
            
            contactRepository.updateContact(contactId, updateDTO)
                .onSuccess { response ->
                    val updatedContact = response.data.toContact()
                    _state.update { 
                        it.copy(
                            isUpdating = false,
                            isEditModalOpen = false,
                            contact = updatedContact.copy(
                                contactUser = updatedContact.contactUser ?: it.contact?.contactUser
                            )
                        ) 
                    }
                }
                .onError { error ->
                    _state.update { 
                        it.copy(
                            isUpdating = false,
                            updateError = error::class.simpleName ?: "Failed to update contact"
                        ) 
                    }
                }
        }
    }
}
