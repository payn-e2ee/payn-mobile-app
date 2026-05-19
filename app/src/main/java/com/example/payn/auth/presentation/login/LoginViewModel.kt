package com.example.payn.auth.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.payn.auth.data.repository.AuthRepository
import com.example.payn.core.data.AuthSessionManager
import com.example.payn.core.data.CryptoManager
import com.example.payn.core.data.DatabaseProvider
import com.example.payn.core.data.KeyValueStorage
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.FormErrors
import com.example.payn.core.domain.IdentityKeysEntity
import com.example.payn.core.domain.onError
import com.example.payn.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters

class LoginViewModel(
    private val databaseProvider: DatabaseProvider,
    private val authRepository: AuthRepository,
    private val keyValueStorage: KeyValueStorage,
    private val authSessionManager: AuthSessionManager,
    private val cryptoManager: CryptoManager
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

    fun submit(context: Context, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        setIsLoading(true)

        viewModelScope.launch {
            authRepository.login(
                state.value.username,
                state.value.password
            ).onSuccess { response ->
                val user = response.data.user
                val deviceRegistrationToken = response.data.accessToken
                databaseProvider.createOrSwitchDatabase(context, user.id)

                val identityKeyEntity =
                    databaseProvider.appDatabase?.identityKeysDao()?.getIdentityKey()
                var identityKey = identityKeyEntity?.publicKey ?: ""
                if (identityKeyEntity == null) {
                    val identityKeyPair = cryptoManager.generateX25519KeyPair()
                    val privateKey = identityKeyPair.private as X25519PrivateKeyParameters
                    val publicKey = identityKeyPair.public as X25519PublicKeyParameters
                    val privateKeyEncrypted =
                        cryptoManager.encrypt(privateKey.encoded)
                    val privateKeyEncryptedBase64 = android.util.Base64.encodeToString(
                        privateKeyEncrypted,
                        android.util.Base64.DEFAULT
                    )
                    val publicKeyBase64 = android.util.Base64.encodeToString(
                        publicKey.encoded,
                        android.util.Base64.DEFAULT
                    )

                    identityKey = publicKeyBase64

                    databaseProvider.appDatabase?.identityKeysDao()?.insert(
                        IdentityKeysEntity(
                            encryptedPrivateKey = privateKeyEncryptedBase64,
                            publicKey = publicKeyBase64,
                        )
                    )
                }

                authRepository.registerDevice(deviceRegistrationToken, identityKey).onSuccess {
                    keyValueStorage.putString("access_token", it.data.accessToken)
                    authSessionManager.initializeSession()
                    onSuccess()
                }
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