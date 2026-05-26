package com.example.payn.chat.domain

data class RatchetEpoch(
    val rootKey: ByteArray,
    val chainKey: ByteArray,
    val remoteEphemeralPublicKey: ByteArray,
    val localEphemeralPublicKey: ByteArray,
    val localEncryptedEphemeralPrivateKey: ByteArray,
)
