package com.example.payn.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MessageTypeDTO(val value: String) {
    @SerialName("text")
    TEXT("text"),

    @SerialName("file")
    FILE("file"),

    @SerialName("image")
    IMAGE("image"),

    @SerialName("video")
    VIDEO("video"),

    @SerialName("voice")
    VOICE("voice")
}