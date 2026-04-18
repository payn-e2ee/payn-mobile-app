package com.example.payn.chat.data.mappers

import com.example.payn.chat.data.dto.MessageDTO
import com.example.payn.chat.domain.Message

fun MessageDTO.toMessage(): Message {
    return Message(
        id = id,
        chatId = chatId,
        userId = userId,
        deviceId = deviceId,
        status = status,
        createdAt = createdAt,
        messageDeliveries = messageDeliveries.map { it.toMessageDelivery() }
    )
}