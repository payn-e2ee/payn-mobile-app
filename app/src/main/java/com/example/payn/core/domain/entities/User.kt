package com.example.payn.core.domain.entities

data class User(
    private val id: String,
    private val username: String,
    private val firstName: String,
    private val lastName: String,
    private val phoneNumber: String,
)