package com.example.payn.settings.presentation.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.payn.core.data.AuthSessionManager
import com.example.payn.core.data.FileManager
import com.example.payn.core.data.dto.UpdateUserFormDTO
import com.example.payn.core.data.repository.UserRepository
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.FormErrors
import com.example.payn.core.domain.onError
import com.example.payn.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val authSessionManager: AuthSessionManager,
    private val fileManager: FileManager
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state = _state
        .onStart {
            loadCurrentUser()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private fun loadCurrentUser() {
        val user = authSessionManager.getUser()
        if (user != null) {
            _state.update {
                it.copy(
                    firstname = user.firstname,
                    lastname = user.lastname,
                    username = user.username,
                    phoneNumber = user.phoneNumber,
                    profileImageId = user.profileImageId
                )
            }
        }
    }

    fun onImageSelected(uri: android.net.Uri) {
        val bytes = fileManager.readBytesFromUri(uri)
        if (bytes != null) {
            _state.update {
                it.copy(profileImageBytes = bytes)
            }
        }
    }

    fun onFirstNameChange(name: String) {
        _state.update {
            it.copy(firstname = name)
        }
    }

    fun onLastNameChange(name: String) {
        _state.update {
            it.copy(lastname = name)
        }
    }

    fun onUsernameChange(username: String) {
        _state.update {
            it.copy(username = username)
        }
    }

    fun setFieldErrors(formErrors: FormErrors) {
        _state.update {
            it.copy(
                usernameFieldError = formErrors.fieldErrors["username"]
                    ?.joinToString("\n") ?: "",
                firstnameFieldError = formErrors.fieldErrors["firstname"]
                    ?.joinToString("\n") ?: "",
                lastnameFieldError = formErrors.fieldErrors["lastname"]
                    ?.joinToString("\n") ?: ""
            )
        }
    }

    fun onSave(onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            userRepository.updateCurrentUser(
                updateUserFormDTO = UpdateUserFormDTO(
                    username = _state.value.username,
                    firstname = _state.value.firstname,
                    lastname = _state.value.lastname,
                ),
                profileImageBytes = _state.value.profileImageBytes
            ).onSuccess {
                authSessionManager.initializeSession()
                _state.update { it.copy(isLoading = false, profileImageBytes = null) }
                onSuccess()
            }.onError { err ->
                _state.update { it.copy(isLoading = false) }
                when (err) {
                    is DataError.Remote.BAD_REQUEST -> {
                        onError(err.message)
                        if (err.formErrors != null) {
                            setFieldErrors(err.formErrors)
                        }
                    }
                    DataError.Remote.UNAUTHORIZED -> onError("Session expired. Please login again.")
                    DataError.Remote.FORBIDDEN -> onError("You don't have permission to do this.")
                    else -> onError("Something went wrong")
                }
            }
        }
    }
}
