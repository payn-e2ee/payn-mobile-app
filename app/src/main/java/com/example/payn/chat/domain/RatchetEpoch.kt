package com.example.payn.chat.domain

data class RatchetEpoch(
    val rootKey: String,
    val chainKey: String,
    val remoteEphemeralPublicKey: String,
    val localEphemeralPublicKey: String,
    val localEncryptedEphemeralPrivateKey: String,
)
