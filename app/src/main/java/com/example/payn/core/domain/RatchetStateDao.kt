package com.example.payn.core.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.payn.core.domain.models.Device

@Dao
interface RatchetStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RatchetStateEntity)

    @Query("SELECT * FROM ratchet_states WHERE deviceId = :deviceId limit 1")
    suspend fun getRatchetStateByDeviceId(deviceId: String): RatchetStateEntity?

    @Query("SELECT * FROM ratchet_states WHERE remoteEphemeralPublicKey = :remoteEphemeralPublicKey limit 1")
    suspend fun getByRemoteEphemeralPublicKey(remoteEphemeralPublicKey: String): RatchetStateEntity?

    @Query("UPDATE ratchet_states SET sendChainKey=:sendChainKey, receiveChainKey=:receiveChainKey, sendMessageCounter=:sendMessageCounter, receiveMessageCounter=:receiveMessageCounter, remoteEphemeralPublicKey=:remoteEphemeralPublicKey, localSendEphemeralPublicKey=:localSendEphemeralPublicKey, localSendEncryptedEphemeralPrivateKey=:localSendEncryptedEphemeralPrivateKey, localReceiveEphemeralPublicKey=:localReceiveEphemeralPublicKey, localReceiveEncryptedEphemeralPrivateKey=:localReceiveEncryptedEphemeralPrivateKey WHERE deviceId = :deviceId")
    suspend fun updateRatchetStateByDeviceId(
        deviceId: String,
        sendChainKey: String,
        receiveChainKey: String,
        sendMessageCounter: Int,
        receiveMessageCounter: Int,
        remoteEphemeralPublicKey: String,
        localSendEphemeralPublicKey: String,
        localSendEncryptedEphemeralPrivateKey: String,
        localReceiveEphemeralPublicKey: String,
        localReceiveEncryptedEphemeralPrivateKey: String,
    )

    @Query("UPDATE ratchet_states SET sendChainKey=:sendChainKey, sendMessageCounter=:sendMessageCounter WHERE deviceId = :deviceId")
    suspend fun updateSendChainStateByDeviceId(
        deviceId: String,
        sendChainKey: String,
        sendMessageCounter: Int,
    )

    @Query("UPDATE ratchet_states SET receiveChainKey=:receiveChainKey, receiveMessageCounter=:receiveMessageCounter WHERE deviceId = :deviceId")
    suspend fun updateReceiveChainStateByDeviceId(
        deviceId: String,
        receiveChainKey: String,
        receiveMessageCounter: Int,
    )

    @Query("SELECT * FROM ratchet_states")
    suspend fun getRatchetStates(): List<RatchetStateEntity>


    @Query(value = "DELETE FROM ratchet_states")
    suspend fun clear()
}