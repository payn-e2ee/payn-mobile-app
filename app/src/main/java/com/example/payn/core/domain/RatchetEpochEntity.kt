package com.example.payn.core.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "ratchet_epochs")
data class RatchetEpochEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val deviceId: String,

    val sendChainKey: String,
    val receiveChainKey: String,

    val sendRootKey: String,
    val receiveRootKey: String,

    val remoteEphemeralPublicKey: String,

    val localSendEphemeralPublicKey: String,
    val localSendEncryptedEphemeralPrivateKey: String,

    val localReceiveEphemeralPublicKey: String,
    val localReceiveEncryptedEphemeralPrivateKey: String,

    val createdAt: Long = System.currentTimeMillis()
)