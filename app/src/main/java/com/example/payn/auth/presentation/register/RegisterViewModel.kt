package com.example.payn.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.payn.auth.data.dto.RegisterFormDTO
import com.example.payn.auth.data.repository.AuthRepository
import com.example.payn.core.data.AppDatabase
import com.example.payn.core.data.KeyValueStorage
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.FormErrors
import com.example.payn.core.domain.onError
import com.example.payn.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val appDatabase: AppDatabase,
    private val authRepository: AuthRepository,
    private val keyValueStorage: KeyValueStorage
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterFormState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    fun onPhoneChange(newValue: String) {
        _state.update { it.copy(phone = newValue, phoneFieldError = "") }
    }

    fun onOtpChange(newValue: String) {
        _state.update { it.copy(otp = newValue, otpFieldError = "") }
    }

    fun onFirstNameChange(newValue: String) {
        _state.update { it.copy(firstName = newValue, firstNameFieldError = "") }
    }

    fun onLastNameChange(newValue: String) {
        _state.update { it.copy(lastName = newValue, lastNameFieldError = "") }
    }

    fun onPasswordChange(newValue: String) {
        _state.update { it.copy(password = newValue, passwordFieldError = "") }
    }

    fun onUsernameChange(newValue: String) {
        _state.update { it.copy(username = newValue, usernameFieldError = "") }
    }

    fun previousStep() {
        _state.update { it.copy(step = (it.step - 1).coerceAtLeast(1)) }
    }

    private fun setIsLoading(value: Boolean) {
        _state.update { it.copy(isLoading = value) }
    }

    private fun setFieldErrors(formErrors: FormErrors) {
        _state.update { it.copy(
            phoneFieldError = formErrors.fieldErrors["phone_number"]?.joinToString("\n") ?: "",
            otpFieldError = formErrors.fieldErrors["code"]?.joinToString("\n") ?: "",
            usernameFieldError = formErrors.fieldErrors["username"]?.joinToString("\n") ?: "",
            firstNameFieldError = formErrors.fieldErrors["firstname"]?.joinToString("\n") ?: "",
            lastNameFieldError = formErrors.fieldErrors["lastname"]?.joinToString("\n") ?: "",
            passwordFieldError = formErrors.fieldErrors["password"]?.joinToString("\n") ?: ""
        ) }
    }

    fun sendOtp(onError: (String) -> Unit) {
        setIsLoading(true)
        viewModelScope.launch {
            authRepository.sendOtp(state.value.phone)
                .onSuccess {
                    _state.update { it.copy(step = 2) }
                }
                .onError { err ->
                    handleRemoteError(err, onError)
                }
            setIsLoading(false)
        }
    }

    fun verifyOtp(onError: (String) -> Unit) {
        setIsLoading(true)
        viewModelScope.launch {
            val otpCode = state.value.otp.toIntOrNull() ?: 0
            authRepository.verifyOtp(state.value.phone, otpCode)
                .onSuccess { response ->
                    _state.update { state ->
                        state.copy(
                            step = 3,
                            verificationToken = response.verificationToken
                        )
                    }
                }
                .onError { err ->
                    handleRemoteError(err, onError)
                }
            setIsLoading(false)
        }
    }

    fun submit(onSuccess: () -> Unit, onError: (String) -> Unit) {
        setIsLoading(true)
        viewModelScope.launch {
            val request = RegisterFormDTO(
                phone_number = state.value.phone,
                username = state.value.username,
                firstname = state.value.firstName,
                lastname = state.value.lastName,
                password = state.value.password,
                verification_token = state.value.verificationToken
            )

            authRepository.register(request)
                .onSuccess { response ->
                    onSuccess()
                }
                .onError { err ->
                    handleRemoteError(err, onError)
                }
            setIsLoading(false)
        }
    }

    private fun handleRemoteError(err: DataError, onError: (String) -> Unit) {
        if (err is DataError.Remote.BAD_REQUEST) {
            onError(err.message)
            err.formErrors?.let { setFieldErrors(it) }
        } else {
            onError("Something went wrong")
        }
    }
}
