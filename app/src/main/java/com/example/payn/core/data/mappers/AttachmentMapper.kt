package com.example.payn.core.data.mappers

import com.example.payn.core.data.dto.AttachmentDTO
import com.example.payn.core.domain.models.Attachment

fun AttachmentDTO.toAttachment(): Attachment {
    return Attachment(
        id = id,
        bucketName = bucketName,
        objectName = objectName,
        originalFileName = originalFileName,
        originalFileSize = originalFileSize,
        createdAt = createdAt,
    )
}