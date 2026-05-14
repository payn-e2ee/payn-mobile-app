package com.example.payn.chat.domain

enum class MessageStatus(val value: String) {
    SENT("sent"),
    DELIVERED("delivered"),
    SEEN("seen"),
}