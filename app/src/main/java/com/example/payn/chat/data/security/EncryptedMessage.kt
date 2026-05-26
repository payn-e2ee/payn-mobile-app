package com.example.payn.chat.data.security

data class EncryptedMessage(
    val ciphertext: ByteArray,
    val ephemeralPublicKey: ByteArray,
    val messageCounter: Int
)