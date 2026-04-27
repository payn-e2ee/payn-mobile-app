package com.example.payn.chat.data.mappers

import com.example.payn.chat.data.dto.ChatDTO
import com.example.payn.chat.domain.Chat

fun ChatDTO.toChat(): Chat {
    return Chat(
        id = id,
        chatMembers = chatMembers?.map { it.toChatMember() },
        messages = messages?.map { it.toMessage() },
        createdAt = createdAt
    )
}