package com.example.payn.core.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "identity_keys")
data class IdentityKeysEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    // Public key (safe to store)
    val publicKey: String,

    // Private key (must be encrypted before storing)
    val encryptedPrivateKey: String,

    val createdAt: Long = System.currentTimeMillis()
)