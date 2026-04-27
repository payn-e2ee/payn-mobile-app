package com.example.payn.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageFrameDTO(
    val header: MessageHeaderDTO,

    val ciphertext: String,

    @SerialName("auth_tag")
    val authTag: String
)