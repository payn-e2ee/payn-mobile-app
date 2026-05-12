package com.example.payn.core.data.repository

import com.example.payn.core.data.dto.AttachmentDTO
import com.example.payn.core.data.dto.UploadAttachmentFormDTO
import com.example.payn.core.data.network.AttachmentDataSource
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result

class AttachmentRepository(
    private val attachmentDataSource: AttachmentDataSource,
) {
    suspend fun uploadAttachment(
        uploadAttachmentFormDTO: UploadAttachmentFormDTO,
        fileBytes: ByteArray
    ): Result<ApiResponse<AttachmentDTO>, DataError.Remote> {
        return attachmentDataSource.uploadAttachment(uploadAttachmentFormDTO, fileBytes)
    }

    suspend fun getAttachmentById(attachmentId: String): Result<ByteArray, DataError.Remote> {
        return attachmentDataSource.getAttachmentById(attachmentId)
    }
}