package com.example.payn.core.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "identity_keys")
data class IdentityKeysEntity(

    @PrimaryKey
    val id: String = "default",

    // Public key (safe to store)
    val publicKey: String,

    // Private key (must be encrypted before storing)
    val encryptedPrivateKey: String,

    val createdAt: Long = System.currentTimeMillis()
)