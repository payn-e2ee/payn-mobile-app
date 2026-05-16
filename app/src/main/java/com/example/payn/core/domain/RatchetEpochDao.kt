package com.example.payn.core.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RatchetEpochDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RatchetEpochEntity)

    @Query("SELECT * FROM ratchet_epochs WHERE remoteEphemeralPublicKey = :remoteEphemeralPublicKey limit 1")
    suspend fun getRatchetEpochByRemoteEphemeralPublicKey(remoteEphemeralPublicKey: String): RatchetEpochEntity?

    @Query("SELECT * FROM ratchet_epochs WHERE localSendEphemeralPublicKey = :localSendEphemeralPublicKey limit 1")
    suspend fun getRatchetEpochByLocalSendEphemeralPublicKey(localSendEphemeralPublicKey: String): RatchetEpochEntity?

    @Query("SELECT * FROM ratchet_epochs WHERE deviceId = :deviceId limit 1")
    suspend fun getRatchetEpochByDeviceId(deviceId: String): RatchetEpochEntity?

    @Query("SELECT * FROM ratchet_epochs")
    suspend fun getRatchetEpochs(): List<RatchetEpochEntity>


    @Query(value = "DELETE FROM ratchet_epochs")
    suspend fun clear()
}