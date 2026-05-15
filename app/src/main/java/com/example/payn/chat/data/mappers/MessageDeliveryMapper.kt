package com.example.payn.chat.data.mappers

import com.example.payn.chat.data.dto.MessageDeliveryDTO
import com.example.payn.chat.domain.MessageDelivery
import com.example.payn.core.data.mappers.toAttachment

fun MessageDeliveryDTO.toMessageDelivery(): MessageDelivery {
    return MessageDelivery(
        id = id,
        messageId = messageId,
        senderDeviceId = senderDeviceId,
        senderUserId = senderUserId,
        recipientDeviceId = recipientDeviceId,
        recipientUserId = recipientUserId,
        ciphertext = ciphertext,
        authTag = authTag,
        ephemeralPublicKey = ephemeralPublicKey,
        type = type.toMessageType(),
        attachmentId = attachmentId,
        messageCounter = messageCounter,
        createdAt = createdAt,

        attachment = attachment?.toAttachment()
    )
}