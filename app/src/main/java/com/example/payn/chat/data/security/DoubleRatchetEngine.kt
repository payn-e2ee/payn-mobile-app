package com.example.payn.chat.data.security

import com.example.payn.chat.domain.RatchetEpoch
import com.example.payn.core.data.CryptoManager
import com.example.payn.core.data.DatabaseProvider
import com.example.payn.core.domain.RatchetEpochEntity
import com.example.payn.core.domain.RatchetStateEntity
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters

class DoubleRatchetEngine(
    val databaseProvider: DatabaseProvider,
    val cryptoManager: CryptoManager
) {
    suspend fun encryptMessage(
        content: ByteArray,
        remoteDeviceId: String,
        remoteIdentityKey: ByteArray,
    ): EncryptedMessage {
        var ratchetStateEntity =
            databaseProvider.appDatabase.ratchetStateDao()
                .getRecentRatchetStateByDeviceId(remoteDeviceId)
        if (ratchetStateEntity == null) {
            ratchetStateEntity = initializeSenderSession(
                remoteDeviceId = remoteDeviceId,
                remoteIdentityKey = remoteIdentityKey
            )
        }

        val ciphertext = encryptAndAdvanceSendChain(
            content = content,
            remoteDeviceId = remoteDeviceId,
            sendChainKey = ratchetStateEntity.sendChainKey,
            sendMessageCounter = ratchetStateEntity.sendMessageCounter
        )

        return EncryptedMessage(
            ciphertext = ciphertext,
            ephemeralPublicKey = ratchetStateEntity.localSendEphemeralPublicKey,
            messageCounter = ratchetStateEntity.sendMessageCounter,
        )
    }

    suspend fun decryptMessage(
        ciphertext: ByteArray,
        remoteEphemeralPublicKey: ByteArray,
        remoteDeviceId: String,
        messageCounter: Int
    ): ByteArray {
        var ratchetStateEntity =
            databaseProvider.appDatabase.ratchetStateDao()
                .getByRemoteEphemeralPublicKey(remoteEphemeralPublicKey)

        var receiveChainKey = ratchetStateEntity?.receiveChainKey

        if (ratchetStateEntity == null) {
            ratchetStateEntity =
                databaseProvider.appDatabase.ratchetStateDao()
                    .getRecentRatchetStateByDeviceId(remoteDeviceId)
            ratchetStateEntity = if (ratchetStateEntity == null) {
                initializeReceiverSession(
                    remoteDeviceId = remoteDeviceId,
                    remoteEphemeralPublicKey = remoteEphemeralPublicKey
                )
            } else {
                performDHRatchetStep(
                    localSendEphemeralPublicKey = ratchetStateEntity.localSendEphemeralPublicKey,
                    localSendEncryptedEphemeralPrivateKey = ratchetStateEntity.localSendEncryptedEphemeralPrivateKey,
                    remoteEphemeralPublicKey = remoteEphemeralPublicKey,
                    remoteDeviceId = remoteDeviceId
                )
            }
            var chainKey = ratchetStateEntity.receiveChainKey!!
            if (messageCounter > 1) {
                var counter = messageCounter

                // Note: 'counter > 2' because decryptAndAdvanceReceiveChain will do the remaining step to make it sync
                while (counter > 2) {
                    val newChainKeyAndMessageKey = cryptoManager.deriveKeyViaHKDF(
                        inputKey = chainKey,
                        salt = null,
                        info = null,
                        outputLength = 64
                    )
                    chainKey = newChainKeyAndMessageKey.sliceArray(0 until 32)
                    counter -= 1
                }
                val newChainKeyAndMessageKey = cryptoManager.deriveKeyViaHKDF(
                    inputKey = chainKey,
                    salt = null,
                    info = null,
                    outputLength = 64
                )

                chainKey = newChainKeyAndMessageKey.sliceArray(0 until 32)
            }
            receiveChainKey = chainKey
        }

        return decryptAndAdvanceReceiveChain(
            ciphertext = ciphertext,
            remoteDeviceId = remoteDeviceId,
            receiveChainKey = receiveChainKey!!,
            receiveMessageCounter = messageCounter,
        )
    }

    val messageKeysCache: HashMap<ByteArray, HashMap<Int, ByteArray>> = HashMap()
    val cacheIndex = 3

    suspend fun decryptStateless(
        ciphertext: ByteArray,
        ephemeralPublicKey: ByteArray,
        remoteDeviceId: String,
        messageCounter: Int,
        isFromCurrentDevice: Boolean
    ): ByteArray {
        var chainKey: ByteArray? = null
        var counter: Int = messageCounter
        val messageCountersMap = messageKeysCache[ephemeralPublicKey]
        if (messageCountersMap != null) {
            val index = (messageCounter / cacheIndex) * cacheIndex
            chainKey = messageCountersMap[index]
        } else {
            messageKeysCache[ephemeralPublicKey] = HashMap()
        }

        if (chainKey == null) {
            val ratchetEpochEntity = if (isFromCurrentDevice) {
                databaseProvider.appDatabase.ratchetEpochDao()
                    .getRatchetEpochByLocalSendEphemeralPublicKeyAndDeviceId(
                        ephemeralPublicKey,
                        remoteDeviceId
                    )
            } else {
                databaseProvider.appDatabase.ratchetEpochDao()
                    .getRatchetEpochByRemoteEphemeralPublicKey(ephemeralPublicKey)
            }
            chainKey =
                if (isFromCurrentDevice) ratchetEpochEntity?.sendChainKey else ratchetEpochEntity?.receiveChainKey
        } else {
            counter = (messageCounter % cacheIndex) + 1
        }

        if (chainKey != null) {
            return decryptAtCounter(
                ciphertext = ciphertext,
                chainKey = chainKey,
                messageCounter = counter,
                ephemeralPublicKey = ephemeralPublicKey
            )
        }

        return "No ratchet epoch found".toByteArray()
    }

    suspend fun isFirstTimeSeeingEphemeralPublicKey(
        remoteEphemeralPublicKey: ByteArray
    ): Boolean {
        return databaseProvider.appDatabase.ratchetEpochDao()
            .getRatchetEpochByRemoteEphemeralPublicKey(remoteEphemeralPublicKey) == null
    }

    private suspend fun initializeSenderSession(
        remoteDeviceId: String,
        remoteIdentityKey: ByteArray
    ): RatchetStateEntity {
        val ratchetEpoch = initialRatchetEpoch(remoteIdentityKey)

        // Store session
        val ratchetStateEntity = RatchetStateEntity(
            deviceId = remoteDeviceId,

            sendChainKey = ratchetEpoch.chainKey,
            sendMessageCounter = 1,
            localSendEphemeralPublicKey = ratchetEpoch.localEphemeralPublicKey,
            localSendEncryptedEphemeralPrivateKey = ratchetEpoch.localEncryptedEphemeralPrivateKey,

            receiveChainKey = null,
            receiveMessageCounter = 1,
            localReceiveEphemeralPublicKey = null,
            localReceiveEncryptedEphemeralPrivateKey = null,

            remoteEphemeralPublicKey = remoteIdentityKey,
        )

        databaseProvider.appDatabase.ratchetStateDao().insert(ratchetStateEntity)

        // store epoch
        databaseProvider.appDatabase.ratchetEpochDao().insert(
            RatchetEpochEntity(
                deviceId = remoteDeviceId,

                sendChainKey = ratchetEpoch.chainKey,
                sendRootKey = ratchetEpoch.rootKey,
                localSendEphemeralPublicKey = ratchetEpoch.localEphemeralPublicKey,
                localSendEncryptedEphemeralPrivateKey = ratchetEpoch.localEncryptedEphemeralPrivateKey,

                receiveChainKey = null,
                receiveRootKey = null,
                localReceiveEphemeralPublicKey = null,
                localReceiveEncryptedEphemeralPrivateKey = null,
                remoteEphemeralPublicKey = ratchetEpoch.remoteEphemeralPublicKey,
            )
        )

        return ratchetStateEntity
    }

    private suspend fun initializeReceiverSession(
        remoteDeviceId: String,
        remoteEphemeralPublicKey: ByteArray
    ): RatchetStateEntity {
        val receiveRatchetEpoch = initialRatchetEpoch(remoteEphemeralPublicKey)
        val sendRatchetEpoch = newRatchetEpoch(remoteEphemeralPublicKey)

        // Store session
        val ratchetStateEntity = RatchetStateEntity(
            deviceId = remoteDeviceId,

            sendChainKey = sendRatchetEpoch.chainKey,
            sendMessageCounter = 1,
            localSendEphemeralPublicKey = sendRatchetEpoch.localEphemeralPublicKey,
            localSendEncryptedEphemeralPrivateKey = sendRatchetEpoch.localEncryptedEphemeralPrivateKey,

            receiveChainKey = receiveRatchetEpoch.chainKey,
            receiveMessageCounter = 1,
            localReceiveEphemeralPublicKey = receiveRatchetEpoch.localEphemeralPublicKey,
            localReceiveEncryptedEphemeralPrivateKey = receiveRatchetEpoch.localEncryptedEphemeralPrivateKey,


            remoteEphemeralPublicKey = remoteEphemeralPublicKey,
        )
        databaseProvider.appDatabase.ratchetStateDao().insert(ratchetStateEntity)

        // Store epoch
        databaseProvider.appDatabase.ratchetEpochDao().insert(
            RatchetEpochEntity(
                deviceId = remoteDeviceId,

                sendChainKey = sendRatchetEpoch.chainKey,
                sendRootKey = sendRatchetEpoch.rootKey,
                localSendEphemeralPublicKey = sendRatchetEpoch.localEphemeralPublicKey,
                localSendEncryptedEphemeralPrivateKey = sendRatchetEpoch.localEncryptedEphemeralPrivateKey,


                receiveChainKey = receiveRatchetEpoch.chainKey,
                receiveRootKey = receiveRatchetEpoch.rootKey,
                localReceiveEphemeralPublicKey = receiveRatchetEpoch.localEphemeralPublicKey,
                localReceiveEncryptedEphemeralPrivateKey = receiveRatchetEpoch.localEncryptedEphemeralPrivateKey,

                remoteEphemeralPublicKey = remoteEphemeralPublicKey
            )
        )

        return ratchetStateEntity
    }


    private suspend fun performDHRatchetStep(
        localSendEphemeralPublicKey: ByteArray,
        localSendEncryptedEphemeralPrivateKey: ByteArray,
        remoteEphemeralPublicKey: ByteArray,
        remoteDeviceId: String,
    ): RatchetStateEntity {
        val receiveRatchetEpoch = newRatchetEpochWithOldKeys(
            localEphemeralPublicKey = localSendEphemeralPublicKey,
            localEncryptedEphemeralPrivateKey = localSendEncryptedEphemeralPrivateKey,
            remoteEphemeralPublicKey = remoteEphemeralPublicKey,
        )

        val sendRatchetEpoch = newRatchetEpoch(
            remoteEphemeralPublicKey = remoteEphemeralPublicKey,
        )

        // Update session
        val ratchetStateEntity = RatchetStateEntity(
            deviceId = remoteDeviceId,

            sendChainKey = sendRatchetEpoch.chainKey,
            sendMessageCounter = 1,
            localSendEphemeralPublicKey = sendRatchetEpoch.localEphemeralPublicKey,
            localSendEncryptedEphemeralPrivateKey = sendRatchetEpoch.localEncryptedEphemeralPrivateKey,

            receiveMessageCounter = 1,
            receiveChainKey = receiveRatchetEpoch.chainKey,
            localReceiveEphemeralPublicKey = receiveRatchetEpoch.localEphemeralPublicKey,
            localReceiveEncryptedEphemeralPrivateKey = receiveRatchetEpoch.localEncryptedEphemeralPrivateKey,

            remoteEphemeralPublicKey = remoteEphemeralPublicKey,
        )

        databaseProvider.appDatabase.ratchetStateDao().updateRatchetStateByDeviceId(
            deviceId = remoteDeviceId,

            sendChainKey = sendRatchetEpoch.chainKey,
            sendMessageCounter = 1,
            localSendEphemeralPublicKey = sendRatchetEpoch.localEphemeralPublicKey,
            localSendEncryptedEphemeralPrivateKey = sendRatchetEpoch.localEncryptedEphemeralPrivateKey,

            receiveChainKey = receiveRatchetEpoch.chainKey,
            receiveMessageCounter = 1,
            localReceiveEphemeralPublicKey = receiveRatchetEpoch.localEphemeralPublicKey,
            localReceiveEncryptedEphemeralPrivateKey = receiveRatchetEpoch.localEncryptedEphemeralPrivateKey,

            remoteEphemeralPublicKey = remoteEphemeralPublicKey,
        )

        // Store epoch
        databaseProvider.appDatabase.ratchetEpochDao().insert(
            RatchetEpochEntity(
                deviceId = remoteDeviceId,

                sendChainKey = sendRatchetEpoch.chainKey,
                sendRootKey = sendRatchetEpoch.rootKey,
                localSendEphemeralPublicKey = sendRatchetEpoch.localEphemeralPublicKey,
                localSendEncryptedEphemeralPrivateKey = sendRatchetEpoch.localEncryptedEphemeralPrivateKey,

                receiveChainKey = receiveRatchetEpoch.chainKey,
                receiveRootKey = receiveRatchetEpoch.rootKey,
                localReceiveEphemeralPublicKey = receiveRatchetEpoch.localEphemeralPublicKey,
                localReceiveEncryptedEphemeralPrivateKey = receiveRatchetEpoch.localEncryptedEphemeralPrivateKey,

                remoteEphemeralPublicKey = remoteEphemeralPublicKey,
            )
        )

        return ratchetStateEntity
    }

    private suspend fun encryptAndAdvanceSendChain(
        content: ByteArray,
        remoteDeviceId: String,
        sendChainKey: ByteArray,
        sendMessageCounter: Int,
    ): ByteArray {
        var chainKey = sendChainKey
        val newChainKeyAndMessageKey = cryptoManager.deriveKeyViaHKDF(
            inputKey = chainKey,
            salt = null,
            info = null,
            outputLength = 64
        )
        val messageKey = newChainKeyAndMessageKey.sliceArray(32 until 64)
        val ciphertext = cryptoManager.encryptWithKey(
            data = content,
            key = messageKey
        )

        chainKey = newChainKeyAndMessageKey.sliceArray(0 until 32)

        databaseProvider.appDatabase.ratchetStateDao().updateSendChainStateByDeviceId(
            deviceId = remoteDeviceId,
            sendChainKey = chainKey,
            sendMessageCounter = sendMessageCounter + 1
        )

        return ciphertext
    }

    private suspend fun decryptAndAdvanceReceiveChain(
        ciphertext: ByteArray,
        remoteDeviceId: String,
        receiveChainKey: ByteArray,
        receiveMessageCounter: Int
    ): ByteArray {
        var chainKey = receiveChainKey
        val newChainKeyAndMessageKey = cryptoManager.deriveKeyViaHKDF(
            inputKey = chainKey,
            salt = null,
            info = null,
            outputLength = 64
        )

        val messageKey = newChainKeyAndMessageKey.sliceArray(32 until 64)
        val plaintext = cryptoManager.decryptWithKey(
            data = ciphertext,
            key = messageKey
        )

        chainKey = newChainKeyAndMessageKey.sliceArray(0 until 32)
        databaseProvider.appDatabase.ratchetStateDao().updateReceiveChainStateByDeviceId(
            deviceId = remoteDeviceId,
            receiveChainKey = chainKey,
            receiveMessageCounter = receiveMessageCounter + 1,
        )

        return plaintext
    }

    private fun decryptAtCounter(
        ciphertext: ByteArray,
        chainKey: ByteArray,
        messageCounter: Int,
        ephemeralPublicKey: ByteArray
    ): ByteArray {
        var chainKey = chainKey
        var counter = messageCounter

        while (counter > 1) {
            val newChainKeyAndMessageKey = cryptoManager.deriveKeyViaHKDF(
                inputKey = chainKey,
                salt = null,
                info = null,
                outputLength = 64
            )
            chainKey = newChainKeyAndMessageKey.sliceArray(0 until 32)
            if (counter % cacheIndex == 0) {
                messageKeysCache[ephemeralPublicKey]?.set(counter, chainKey)
            }
            counter -= 1
        }

        val newChainKeyAndMessageKey = cryptoManager.deriveKeyViaHKDF(
            inputKey = chainKey,
            salt = null,
            info = null,
            outputLength = 64
        )

        val messageKey = newChainKeyAndMessageKey.sliceArray(32 until 64)

        val plaintext = cryptoManager.decryptWithKey(
            data = ciphertext,
            key = messageKey
        )

        return plaintext
    }

    private suspend fun initialRatchetEpoch(
        remoteIdentityKey: ByteArray
    ): RatchetEpoch {
        val localIdentityKeys = databaseProvider.appDatabase.identityKeysDao().getIdentityKey()!!

        val privateKey = cryptoManager.decrypt(localIdentityKeys.encryptedPrivateKey)

        val rootKey = cryptoManager.generateSharedSecret(
            privateKey = X25519PrivateKeyParameters(privateKey),
            publicKey = X25519PublicKeyParameters(remoteIdentityKey)
        )

        val chainKey = cryptoManager.deriveKeyViaHKDF(
            inputKey = rootKey,
            salt = null,
            info = null,
            outputLength = 32
        )

        return RatchetEpoch(
            rootKey = rootKey,
            chainKey = chainKey,
            remoteEphemeralPublicKey = remoteIdentityKey,
            localEphemeralPublicKey = localIdentityKeys.publicKey,
            localEncryptedEphemeralPrivateKey = localIdentityKeys.encryptedPrivateKey
        )
    }

    private fun newRatchetEpoch(
        remoteEphemeralPublicKey: ByteArray,
    ): RatchetEpoch {
        val keyPair = cryptoManager.generateX25519KeyPair()
        val privateKey = keyPair.private as X25519PrivateKeyParameters
        val publicKey = keyPair.public as X25519PublicKeyParameters

        val rootKey = cryptoManager.generateSharedSecret(
            privateKey = privateKey,
            publicKey = X25519PublicKeyParameters(remoteEphemeralPublicKey)
        )

        val chainKey = cryptoManager.deriveKeyViaHKDF(
            inputKey = rootKey,
            salt = null,
            info = null,
            outputLength = 32
        )

        return RatchetEpoch(
            rootKey = rootKey,
            chainKey = chainKey,
            remoteEphemeralPublicKey = remoteEphemeralPublicKey,
            localEphemeralPublicKey = publicKey.encoded,
            localEncryptedEphemeralPrivateKey = cryptoManager.encrypt(privateKey.encoded)
        )
    }

    private fun newRatchetEpochWithOldKeys(
        localEphemeralPublicKey: ByteArray,
        localEncryptedEphemeralPrivateKey: ByteArray,
        remoteEphemeralPublicKey: ByteArray
    ): RatchetEpoch {
        val privateKey = cryptoManager.decrypt(localEncryptedEphemeralPrivateKey)

        val rootKey = cryptoManager.generateSharedSecret(
            privateKey = X25519PrivateKeyParameters(privateKey),
            publicKey = X25519PublicKeyParameters(remoteEphemeralPublicKey),
        )

        val chainKey = cryptoManager.deriveKeyViaHKDF(
            inputKey = rootKey,
            salt = null,
            info = null,
            32
        )

        return RatchetEpoch(
            rootKey = rootKey,
            remoteEphemeralPublicKey = remoteEphemeralPublicKey,
            localEphemeralPublicKey = localEphemeralPublicKey,
            localEncryptedEphemeralPrivateKey = localEncryptedEphemeralPrivateKey,
            chainKey = chainKey
        )
    }
}