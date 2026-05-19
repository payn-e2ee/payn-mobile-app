package com.example.payn.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.payn.auth.data.repository.AuthRepository
import com.example.payn.core.data.AppDatabase
import com.example.payn.core.data.AuthSessionManager
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
import kotlinx.coroutines.tasks.await
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log

class LoginViewModel(
    private val appDatabase: AppDatabase,
    private val authRepository: AuthRepository,
    private val keyValueStorage: KeyValueStorage,
    private val authSessionManager: AuthSessionManager
) : ViewModel() {
    private val _state = MutableStateFlow(LoginFormState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    fun onUsernameChange(newUsername: String) {
        _state.update { loginFormState ->
            loginFormState.copy(username = newUsername)
        }
    }

    fun onPasswordChange(newPassword: String) {
        _state.update { loginFormState ->
            loginFormState.copy(password = newPassword)
        }
    }

    fun setIsLoading(value: Boolean) {
        _state.update { loginFormState -> loginFormState.copy(isLoading = value) }
    }

    fun setFieldErrors(formErrors: FormErrors) {
        _state.update { loginFormState ->
            loginFormState.copy(
                usernameFieldError = formErrors.fieldErrors["username"]
                    ?.joinToString("\n") ?: "",
                passwordFieldError = formErrors.fieldErrors["password"]
                    ?.joinToString("\n") ?: ""
            )
        }
    }

    fun submit(onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        setIsLoading(true)

        viewModelScope.launch {
            val identityKey = appDatabase.identityKeysDao().getIdentityKey()?.publicKey ?: ""
            
            val fcmToken = try {
                FirebaseMessaging.getInstance().token.await()
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Failed to get FCM token", e)
                null
            }

            authRepository.login(
                state.value.username,
                state.value.password,
                identityKey,
                fcmToken
            ).onSuccess {
                keyValueStorage.putString("access_token", it.data.accessToken)
                authSessionManager.initializeSession()
                onSuccess()
            }.onError { err ->
                if (err is DataError.Remote.BAD_REQUEST) {
                    onError(err.message)
                    if (err.formErrors != null) {
                        setFieldErrors(err.formErrors)
                    }
                } else {
                    onError("Something went wrong")
                }
            }
            setIsLoading(false)
        }
    }
}