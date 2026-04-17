package com.example.payn.auth.presentation.login

data class LoginFormState(
    var isLoading: Boolean = false,
    var username: String = "",
    var password: String = "",
    var usernameFieldError: String = "",
    var passwordFieldError: String = "",
)