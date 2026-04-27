package com.example.payn.auth.presentation.register

data class RegisterFormState(
    val step: Int = 1,
    val isLoading: Boolean = false,

    val phone: String = "",
    val phoneFieldError: String = "",

    val otp: String = "",
    val otpFieldError: String = "",

    val firstName: String = "",
    val firstNameFieldError: String = "",
    val lastName: String = "",
    val lastNameFieldError: String = "",
    val username: String = "",
    val usernameFieldError: String = "",
    val password: String = "",
    val passwordFieldError: String = "",
    val verificationToken: String = ""
)