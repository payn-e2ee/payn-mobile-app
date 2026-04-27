package com.example.payn.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.payn.core.domain.IdentityKeysDao
import com.example.payn.core.domain.IdentityKeysEntity
import com.example.payn.core.domain.RatchetEpochDao
import com.example.payn.core.domain.RatchetEpochEntity
import com.example.payn.core.domain.RatchetStateDao
import com.example.payn.core.domain.RatchetStateEntity

@Database(
    entities = [IdentityKeysEntity::class, RatchetEpochEntity::class, RatchetStateEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun identityKeysDao(): IdentityKeysDao
    abstract fun ratchetEpochDao(): RatchetEpochDao
    abstract fun ratchetStateDao(): RatchetStateDao
}