package com.example.payn.chat.data.mappers

import com.example.payn.chat.data.dto.MessageStatusDTO
import com.example.payn.chat.domain.MessageStatus

fun MessageStatusDTO.toMessageStatus(): MessageStatus {
    return MessageStatus.valueOf(this.name)
}