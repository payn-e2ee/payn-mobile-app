package com.example.payn.core.domain.models

import com.example.payn.chat.domain.ChatMember

data class User(
    val id: String,
    val username: String,
    val firstname: String,
    val lastname: String,
    val phoneNumber: String,
    val profileImageId: String? = null,
    val devices: List<Device> = emptyList(),
    val chatMembers: List<ChatMember> = emptyList()
)