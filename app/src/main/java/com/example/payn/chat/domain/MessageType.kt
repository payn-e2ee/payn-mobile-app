package com.example.payn.chat.domain

enum class MessageType(val value: String) {
    TEXT("text"),
    FILE("file"),
    IMAGE("image"),
    VIDEO("video"),
    VOICE("voice")
}