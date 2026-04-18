package com.example.payn.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val message: String,
    val data: T
)