package com.example.payn.core.domain.models

data class Attachment(
    val id: String,
    val bucketName: String,
    val objectName: String,
    val originalFileName: String,
    val originalFileSize: Long,
    val createdAt: String
)