package com.example.payn.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
    val errors: FormErrors?
)