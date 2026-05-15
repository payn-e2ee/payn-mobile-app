package com.example.payn.core.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadAttachmentFormDTO(
    @SerialName("original_file_name")
    val originalFileName: String,

    @SerialName("original_file_size")
    val originalFileSize: Long
)