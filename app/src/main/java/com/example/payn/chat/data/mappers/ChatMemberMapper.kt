package com.example.payn.chat.data.mappers

import com.example.payn.chat.data.dto.ChatMemberDTO
import com.example.payn.chat.domain.ChatMember
import com.example.payn.core.data.mappers.toUser

fun ChatMemberDTO.toChatMember(): ChatMember {
    return ChatMember(
        id = id,
        user = user.toUser(),
        createdAt = createdAt
    )
}