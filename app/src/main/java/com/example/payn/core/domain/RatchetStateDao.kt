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

    @Query("SELECT * FROM ratchet_states WHERE deviceId = :deviceId ORDER BY createdAt DESC limit 1")
    suspend fun getRecentRatchetStateByDeviceId(deviceId: String): RatchetStateEntity?

    @Query("SELECT * FROM ratchet_states WHERE remoteEphemeralPublicKey = :remoteEphemeralPublicKey limit 1")
    suspend fun getByRemoteEphemeralPublicKey(remoteEphemeralPublicKey: ByteArray): RatchetStateEntity?

    @Query("UPDATE ratchet_states SET sendChainKey=:sendChainKey, receiveChainKey=:receiveChainKey, sendMessageCounter=:sendMessageCounter, receiveMessageCounter=:receiveMessageCounter, remoteEphemeralPublicKey=:remoteEphemeralPublicKey, localSendEphemeralPublicKey=:localSendEphemeralPublicKey, localSendEncryptedEphemeralPrivateKey=:localSendEncryptedEphemeralPrivateKey, localReceiveEphemeralPublicKey=:localReceiveEphemeralPublicKey, localReceiveEncryptedEphemeralPrivateKey=:localReceiveEncryptedEphemeralPrivateKey WHERE deviceId = :deviceId")
    suspend fun updateRatchetStateByDeviceId(
        deviceId: String,
        sendChainKey: ByteArray,
        receiveChainKey: ByteArray,
        sendMessageCounter: Int,
        receiveMessageCounter: Int,
        remoteEphemeralPublicKey: ByteArray,
        localSendEphemeralPublicKey: ByteArray,
        localSendEncryptedEphemeralPrivateKey: ByteArray,
        localReceiveEphemeralPublicKey: ByteArray,
        localReceiveEncryptedEphemeralPrivateKey: ByteArray,
    )

    @Query("UPDATE ratchet_states SET sendChainKey=:sendChainKey, sendMessageCounter=:sendMessageCounter WHERE deviceId = :deviceId")
    suspend fun updateSendChainStateByDeviceId(
        deviceId: String,
        sendChainKey: ByteArray,
        sendMessageCounter: Int,
    )

    @Query("UPDATE ratchet_states SET receiveChainKey=:receiveChainKey, receiveMessageCounter=:receiveMessageCounter WHERE deviceId = :deviceId")
    suspend fun updateReceiveChainStateByDeviceId(
        deviceId: String,
        receiveChainKey: ByteArray,
        receiveMessageCounter: Int,
    )

    @Query("SELECT * FROM ratchet_states")
    suspend fun getRatchetStates(): List<RatchetStateEntity>


    @Query(value = "DELETE FROM ratchet_states")
    suspend fun clear()
}