package com.example.payn.contact.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateContactDTO(
    val firstname: String,
    val lastname: String
)
