package com.example.payn.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MessageStatusDTO(val value: String) {
    @SerialName("sent")
    SENT("sent"),

    @SerialName("delivered")
    DELIVERED("delivered"),

    @SerialName("seen")
    SEEN("seen"),
}