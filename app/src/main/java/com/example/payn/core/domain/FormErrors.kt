package com.example.payn.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class FormErrors(
    val formErrors: List<String> = emptyList(),
    val fieldErrors: Map<String, List<String>> = emptyMap()
)