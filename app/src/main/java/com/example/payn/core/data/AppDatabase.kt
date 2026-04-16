package com.example.payn.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.payn.core.domain.IdentityKeysDao
import com.example.payn.core.domain.IdentityKeysEntity

@Database(entities = [IdentityKeysEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun identityKeysDao(): IdentityKeysDao
}