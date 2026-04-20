package com.example.payn.contact.domain

import com.example.payn.core.domain.models.User

data class Contact(
    val id: String,
    val userId: String,
    val firstname: String,
    val lastname: String,
    val contactUserId: String,
    val createdAt: String,
    val contactUser: User? = null
)
