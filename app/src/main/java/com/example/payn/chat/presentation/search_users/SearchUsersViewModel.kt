package com.example.payn.chat.presentation.search_users

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.payn.app.Route
import com.example.payn.contact.data.dto.CreateContactDTO
import com.example.payn.contact.data.repository.ContactRepository
import com.example.payn.core.data.repository.UserRepository
import com.example.payn.core.domain.onError
import com.example.payn.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchUsersViewModel(
    private val userRepository: UserRepository,
    private val contactRepository: ContactRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val query = savedStateHandle.toRoute<Route.SearchUsers>().query

    private val _state = MutableStateFlow(SearchUsersState(query = query))
    val state = _state
        .onStart { searchUsers() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value,
        )

    private fun searchUsers() {
        if (query.isBlank()) {
            _state.update { it.copy(error = "Search query is required") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            userRepository.searchUsers(query)
                .onSuccess { response ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            users = response.data,
                        )
                    }
                }
                .onError {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to search users",
                        )
                    }
                }
        }
    }

    fun addContact(user: com.example.payn.core.data.dto.SearchUserDTO) {
        if (user.isContact) return

        viewModelScope.launch {
            _state.update {
                it.copy(addingContactUserId = user.id, addContactError = null)
            }

            val createContactDTO = CreateContactDTO(
                phoneNumber = user.phoneNumber,
                firstname = user.firstname.orEmpty(),
                lastname = user.lastname.orEmpty(),
            )

            contactRepository.createContact(createContactDTO)
                .onSuccess {
                    _state.update { current ->
                        current.copy(
                            addingContactUserId = null,
                            users = current.users.map { found ->
                                if (found.id == user.id) {
                                    found.copy(isContact = true)
                                } else {
                                    found
                                }
                            },
                        )
                    }
                }
                .onError {
                    _state.update {
                        it.copy(
                            addingContactUserId = null,
                            addContactError = "Failed to add contact",
                        )
                    }
                }
        }
    }
}
