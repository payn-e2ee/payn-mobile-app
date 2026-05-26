package com.example.payn.core.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "ratchet_epochs")
data class RatchetEpochEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val deviceId: String,

    val sendChainKey: ByteArray,
    val receiveChainKey: ByteArray?,

    val sendRootKey: ByteArray,
    val receiveRootKey: ByteArray?,

    val remoteEphemeralPublicKey: ByteArray,

    val localSendEphemeralPublicKey: ByteArray,
    val localSendEncryptedEphemeralPrivateKey: ByteArray,

    val localReceiveEphemeralPublicKey: ByteArray?,
    val localReceiveEncryptedEphemeralPrivateKey: ByteArray?,

    val createdAt: Long = System.currentTimeMillis()
)