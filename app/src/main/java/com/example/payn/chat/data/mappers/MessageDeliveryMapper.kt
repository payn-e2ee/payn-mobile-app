package com.example.payn.chat.data.mappers

import com.example.payn.chat.data.dto.MessageDeliveryDTO
import com.example.payn.chat.domain.MessageDelivery

fun MessageDeliveryDTO.toMessageDelivery(): MessageDelivery {
    return MessageDelivery(
        id = id,
        messageId = messageId,
        deviceId = deviceId,
        userId = userId,
        ciphertext = ciphertext,
        authTag = authTag,
        ephemeralPublicKey = ephemeralPublicKey,
        type = type,
        attachmentId = attachmentId,
        createdAt = createdAt
    )
}