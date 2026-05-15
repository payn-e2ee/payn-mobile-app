package com.example.payn.chat.data.mappers

import com.example.payn.chat.data.dto.MessageTypeDTO
import com.example.payn.chat.domain.MessageType

fun MessageTypeDTO.toMessageType(): MessageType {
    return MessageType.valueOf(this.name)
}