package com.example.payn.core.data.mappers

import com.example.payn.chat.data.mappers.toChatMember
import com.example.payn.core.data.dto.UserDTO
import com.example.payn.core.domain.models.User

fun UserDTO.toUser(): User {
    return User(
        id = id,
        username = username,
        firstname = firstname,
        lastname = lastname,
        phoneNumber = phoneNumber,
        profileImageId = profileImageId,
        devices = devices.map { it.toDevice() },
        chatMembers = chatMembers.map { it.toChatMember() }
    )
}