package com.example.payn.core.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IdentityKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: IdentityKeysEntity)

    @Query("SELECT * FROM identity_keys")
    suspend fun getIdentityKey(): IdentityKeysEntity?

    @Query(value = "DELETE FROM identity_keys")
    suspend fun clear()
}