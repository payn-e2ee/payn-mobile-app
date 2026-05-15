package com.example.payn.core.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttachmentDTO(
    val id: String,

    @SerialName("bucket_name")
    val bucketName: String,

    @SerialName("object_name")
    val objectName: String,

    @SerialName("original_file_name")
    val originalFileName: String,

    @SerialName("original_file_size")
    val originalFileSize: Long,

    @SerialName("created_at")
    val createdAt: String

)