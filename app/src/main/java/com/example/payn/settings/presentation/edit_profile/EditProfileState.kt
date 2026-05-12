package com.example.payn.settings.presentation.edit_profile

data class EditProfileState(
    val firstname: String = "",
    var firstnameFieldError: String = "",

    val lastname: String = "",
    var lastnameFieldError: String = "",

    val username: String = "",
    var usernameFieldError: String = "",

    val profileImage: String = "",
    val profileImageId: String? = null,
    val profileImageBytes: ByteArray? = null,

    val phoneNumber: String = "",

    val isLoading: Boolean = false,
    val error: String? = null
)
