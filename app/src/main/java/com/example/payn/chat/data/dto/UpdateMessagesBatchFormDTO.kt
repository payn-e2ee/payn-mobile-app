package com.example.payn.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateMessagesBatchFormDTO(
    @SerialName("message_ids")
    val messageIds: List<String>,

    val status: String
)