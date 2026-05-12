package com.example.payn.core.data.network

import com.example.payn.core.config.AppConfig
import com.example.payn.core.data.dto.AttachmentDTO
import com.example.payn.core.data.dto.UploadAttachmentFormDTO
import com.example.payn.core.data.safeCall
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

private const val BASE_URL = "${AppConfig.BASE_API_URL}/attachments"

class AttachmentDataSource(private val httpClient: HttpClient) {
    suspend fun uploadAttachment(
        uploadAttachmentFormDTO: UploadAttachmentFormDTO,
        fileBytes: ByteArray
    ): Result<ApiResponse<AttachmentDTO>, DataError.Remote> {
        return safeCall {
            httpClient.post(
                urlString = "$BASE_URL/"
            ) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                "file", fileBytes,
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"file\""
                                    )
                                })
                            append("original_file_name", uploadAttachmentFormDTO.originalFileName)
                            append("original_file_size", uploadAttachmentFormDTO.originalFileSize)
                        }
                    )
                )
            }
        }
    }

    suspend fun getAttachmentById(attachmentId: String): Result<ByteArray, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/$attachmentId"
            )
        }
    }
}