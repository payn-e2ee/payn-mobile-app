package com.example.payn.chat.data.security

import android.util.Base64
import android.util.Log
import com.example.payn.chat.domain.RatchetEpoch
import com.example.payn.core.data.AppDatabase
import com.example.payn.core.data.CryptoManager
import com.example.payn.core.domain.RatchetEpochEntity
import com.example.payn.core.domain.RatchetStateEntity
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters

class DoubleRatchetEngine(
    val appDatabase: AppDatabase,
    val cryptoManager: CryptoManager
) {
    suspend fun encryptMessage(
        content: ByteArray,
        remoteDeviceId: String,
        remoteIdentityKey: String,
    ): EncryptedMessage {
        var ratchetStateEntity =
            appDatabase.ratchetStateDao().getRecentRatchetStateByDeviceId(remoteDeviceId)
        if (ratchetStateEntity == null) {
            log("Session establishment")
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
        remoteEphemeralPublicKey: String,
        remoteDeviceId: String,
        messageCounter: Int
    ): ByteArray {
        var ratchetStateEntity =
            appDatabase.ratchetStateDao().getByRemoteEphemeralPublicKey(remoteEphemeralPublicKey)

        var receiveChainKey = ratchetStateEntity?.receiveChainKey

        if (ratchetStateEntity == null) {
            ratchetStateEntity =
                appDatabase.ratchetStateDao().getRecentRatchetStateByDeviceId(remoteDeviceId)
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
            var chainKey = decodeFromBase64(ratchetStateEntity.receiveChainKey)
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
            receiveChainKey = encodeToBase64(chainKey)
        }

        return decryptAndAdvanceReceiveChain(
            ciphertext = ciphertext,
            remoteDeviceId = remoteDeviceId,
            receiveChainKey = receiveChainKey!!,
            receiveMessageCounter = messageCounter,
        )
    }

    suspend fun decryptStateless(
        ciphertext: ByteArray,
        ephemeralPublicKey: String,
        messageCounter: Int,
        isFromCurrentDevice: Boolean
    ): ByteArray {
        log("isFromCurrentDevice=$isFromCurrentDevice")
        log("ephemeralPublicKey=$ephemeralPublicKey")

        val ratchetEpochEntity = if (isFromCurrentDevice) {
            appDatabase.ratchetEpochDao()
                .getRatchetEpochByLocalSendEphemeralPublicKey(ephemeralPublicKey)
        } else {
            appDatabase.ratchetEpochDao()
                .getRatchetEpochByRemoteEphemeralPublicKey(ephemeralPublicKey)
        }

        if (ratchetEpochEntity != null) {
            log("messageCounter=$messageCounter")
            log("=======[${ratchetEpochEntity.id}]=======")
            log("ephemeralPublicKey=${ratchetEpochEntity.remoteEphemeralPublicKey}")
            log("localSendEphemeralPublicKey=${ratchetEpochEntity.localSendEphemeralPublicKey}")
            log("localReceiveEphemeralPublicKey=${ratchetEpochEntity.localReceiveEphemeralPublicKey}")
            return decryptAtCounter(
                ciphertext = ciphertext,
                epochReceiveChainKey = if (isFromCurrentDevice) ratchetEpochEntity.sendChainKey else ratchetEpochEntity.receiveChainKey,
                messageCounter = messageCounter
            )
        }

        return "No ratchet epoch found".toByteArray()
    }

    suspend fun isSessionInitialized(
        deviceId: String
    ): Boolean {
        return appDatabase.ratchetStateDao().getRecentRatchetStateByDeviceId(deviceId) != null
    }

    suspend fun isFirstTimeSeeingEphemeralPublicKey(
        remoteEphemeralPublicKey: String
    ): Boolean {
        return appDatabase.ratchetEpochDao()
            .getRatchetEpochByRemoteEphemeralPublicKey(remoteEphemeralPublicKey) == null
    }

    private suspend fun initializeSenderSession(
        remoteDeviceId: String,
        remoteIdentityKey: String
    ): RatchetStateEntity {
        log("Session establishment")
        val ratchetEpoch = initialRatchetEpoch(remoteIdentityKey)

        // Store session
        val ratchetStateEntity = RatchetStateEntity(
            deviceId = remoteDeviceId,

            sendChainKey = ratchetEpoch.chainKey,
            sendMessageCounter = 1,
            localSendEphemeralPublicKey = ratchetEpoch.localEphemeralPublicKey,
            localSendEncryptedEphemeralPrivateKey = ratchetEpoch.localEncryptedEphemeralPrivateKey,

            receiveChainKey = "",
            receiveMessageCounter = 1,
            localReceiveEphemeralPublicKey = "",
            localReceiveEncryptedEphemeralPrivateKey = "",

            remoteEphemeralPublicKey = remoteIdentityKey,
        )

        appDatabase.ratchetStateDao().insert(ratchetStateEntity)

        // store epoch
        appDatabase.ratchetEpochDao().insert(
            RatchetEpochEntity(
                deviceId = remoteDeviceId,

                sendChainKey = ratchetEpoch.chainKey,
                sendRootKey = ratchetEpoch.rootKey,
                localSendEphemeralPublicKey = ratchetEpoch.localEphemeralPublicKey,
                localSendEncryptedEphemeralPrivateKey = ratchetEpoch.localEncryptedEphemeralPrivateKey,

                receiveChainKey = "",
                receiveRootKey = "",
                localReceiveEphemeralPublicKey = "",
                localReceiveEncryptedEphemeralPrivateKey = "",
                remoteEphemeralPublicKey = ratchetEpoch.remoteEphemeralPublicKey,
            )
        )

        return ratchetStateEntity
    }

    private suspend fun initializeReceiverSession(
        remoteDeviceId: String,
        remoteEphemeralPublicKey: String
    ): RatchetStateEntity {
        log("Session establishment")
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
        appDatabase.ratchetStateDao().insert(ratchetStateEntity)

        // Store epoch
        appDatabase.ratchetEpochDao().insert(
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
        localSendEphemeralPublicKey: String,
        localSendEncryptedEphemeralPrivateKey: String,
        remoteEphemeralPublicKey: String,
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

        appDatabase.ratchetStateDao().updateRatchetStateByDeviceId(
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
        appDatabase.ratchetEpochDao().insert(
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
        sendChainKey: String,
        sendMessageCounter: Int,
    ): ByteArray {
        var chainKey = decodeFromBase64(sendChainKey)
        val newChainKeyAndMessageKey = cryptoManager.deriveKeyViaHKDF(
            inputKey = chainKey,
            salt = null,
            info = null,
            outputLength = 64
        )
        val messageKey = newChainKeyAndMessageKey.sliceArray(32 until 64)

        log("sendChainKey=${encodeToBase64(chainKey)}")
        log("messageKey=${encodeToBase64(messageKey)}")

        val ciphertext = cryptoManager.encryptWithKey(
            data = content,
            key = messageKey
        )

        // Update state
        chainKey = newChainKeyAndMessageKey.sliceArray(0 until 32)
        val chainKeyBase64 = encodeToBase64(chainKey)

        appDatabase.ratchetStateDao().updateSendChainStateByDeviceId(
            deviceId = remoteDeviceId,
            sendChainKey = chainKeyBase64,
            sendMessageCounter = sendMessageCounter + 1
        )

        return ciphertext
    }

    private suspend fun decryptAndAdvanceReceiveChain(
        ciphertext: ByteArray,
        remoteDeviceId: String,
        receiveChainKey: String,
        receiveMessageCounter: Int
    ): ByteArray {
        var chainKey = decodeFromBase64(receiveChainKey)
        val newChainKeyAndMessageKey = cryptoManager.deriveKeyViaHKDF(
            inputKey = chainKey,
            salt = null,
            info = null,
            outputLength = 64
        )
        val messageKey = newChainKeyAndMessageKey.sliceArray(32 until 64)

        log("receiveChainKey=${encodeToBase64(chainKey)}")
        log("messageKey=${encodeToBase64(messageKey)}")
        log("ciphertext=${encodeToBase64(ciphertext)}")
        val plaintext = cryptoManager.decryptWithKey(
            data = ciphertext,
            key = messageKey
        )

        // Update state
        chainKey = newChainKeyAndMessageKey.sliceArray(0 until 32)
        appDatabase.ratchetStateDao().updateReceiveChainStateByDeviceId(
            deviceId = remoteDeviceId,
            receiveChainKey = encodeToBase64(chainKey),
            receiveMessageCounter = receiveMessageCounter + 1,
        )

        return plaintext
    }

    private fun decryptAtCounter(
        ciphertext: ByteArray,
        epochReceiveChainKey: String,
        messageCounter: Int
    ): ByteArray {
        var chainKey = decodeFromBase64(epochReceiveChainKey)
        var counter = messageCounter

        log("chainKey=${encodeToBase64(chainKey)}")
        while (counter > 1) {
            val newChainKeyAndMessageKey = cryptoManager.deriveKeyViaHKDF(
                inputKey = chainKey,
                salt = null,
                info = null,
                outputLength = 64
            )
            chainKey = newChainKeyAndMessageKey.sliceArray(0 until 32)
            log("chainKey=${encodeToBase64(chainKey)}")
            counter -= 1
        }

        log("counter=$counter")
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
        remoteIdentityKey: String
    ): RatchetEpoch {
        log("initialRatchetEpoch:")

        val localIdentityKeys = appDatabase.identityKeysDao().getIdentityKey()!!

        val privateKey = decodeFromBase64(
            key = localIdentityKeys.encryptedPrivateKey,
            isEncrypted = true
        )

        val publicKey = decodeFromBase64(remoteIdentityKey)

        val rootKey = cryptoManager.generateSharedSecret(
            privateKey = X25519PrivateKeyParameters(privateKey),
            publicKey = X25519PublicKeyParameters(publicKey)
        )

        val chainKey = cryptoManager.deriveKeyViaHKDF(
            inputKey = rootKey,
            salt = null,
            info = null,
            outputLength = 32
        )

        val rootKeyBase64 = encodeToBase64(rootKey)
        val chainKeyBase64 = encodeToBase64(chainKey)

        log("remoteIdentityKey='$remoteIdentityKey'")
        log("localIdentityKeys.publicKey='${localIdentityKeys.publicKey}'")
        log("rootKeyBase64='$rootKeyBase64'")
        log("chainKeyBase64='$chainKeyBase64'")

        return RatchetEpoch(
            rootKey = rootKeyBase64,
            chainKey = chainKeyBase64,
            remoteEphemeralPublicKey = remoteIdentityKey,
            localEphemeralPublicKey = localIdentityKeys.publicKey,
            localEncryptedEphemeralPrivateKey = localIdentityKeys.encryptedPrivateKey
        )
    }

    private fun newRatchetEpoch(
        remoteEphemeralPublicKey: String,
    ): RatchetEpoch {
        log("newRatchetEpoch:")

        val keyPair = cryptoManager.generateX25519KeyPair()
        val privateKey = keyPair.private as X25519PrivateKeyParameters
        val publicKey = keyPair.public as X25519PublicKeyParameters
        val remotePublicKey = decodeFromBase64(remoteEphemeralPublicKey)


        val rootKey = cryptoManager.generateSharedSecret(
            privateKey = privateKey,
            publicKey = X25519PublicKeyParameters(remotePublicKey)
        )

        val chainKey = cryptoManager.deriveKeyViaHKDF(
            inputKey = rootKey,
            salt = null,
            info = null,
            outputLength = 32
        )

        val rootKeyBase64 = encodeToBase64(rootKey)
        val chainKeyBase64 = encodeToBase64(chainKey)

        log("remoteEphemeralPublicKey='$remoteEphemeralPublicKey'")
        log("localEphemeralPublicKey='${encodeToBase64(publicKey.encoded)}'")
        log("rootKeyBase64='$rootKeyBase64'")
        log("chainKeyBase64='$chainKeyBase64'")

        return RatchetEpoch(
            rootKey = rootKeyBase64,
            chainKey = chainKeyBase64,
            remoteEphemeralPublicKey = remoteEphemeralPublicKey,
            localEphemeralPublicKey = encodeToBase64(publicKey.encoded),
            localEncryptedEphemeralPrivateKey = encodeToBase64(
                cryptoManager.encrypt(
                    privateKey.encoded
                )
            )
        )
    }

    private fun newRatchetEpochWithOldKeys(
        localEphemeralPublicKey: String,
        localEncryptedEphemeralPrivateKey: String,
        remoteEphemeralPublicKey: String
    ): RatchetEpoch {
        log("newRatchetEpochWithOldKeys:")

        val remotePublicKey = decodeFromBase64(remoteEphemeralPublicKey)
        val privateKey = cryptoManager.decrypt(
            decodeFromBase64(localEncryptedEphemeralPrivateKey)
        )

        val rootKey = cryptoManager.generateSharedSecret(
            privateKey = X25519PrivateKeyParameters(privateKey),
            publicKey = X25519PublicKeyParameters(remotePublicKey),
        )

        val chainKey = cryptoManager.deriveKeyViaHKDF(
            inputKey = rootKey,
            salt = null,
            info = null,
            32
        )

        log("localEphemeralPublicKey=$localEphemeralPublicKey")
        log("localEncryptedEphemeralPrivateKey=$localEncryptedEphemeralPrivateKey")
        log("remoteEphemeralPublicKey=$remoteEphemeralPublicKey")
        log("rootKey=${encodeToBase64(rootKey)}")
        log("chainKey=${encodeToBase64(chainKey)}")

        return RatchetEpoch(
            rootKey = encodeToBase64(rootKey),
            remoteEphemeralPublicKey = remoteEphemeralPublicKey,
            localEphemeralPublicKey = localEphemeralPublicKey,
            localEncryptedEphemeralPrivateKey = localEncryptedEphemeralPrivateKey,
            chainKey = encodeToBase64(chainKey)
        )
    }

    private fun log(message: String) {
        Log.println(
            Log.INFO,
            "DoubleRatchetEngine",
            message
        )
    }

    private fun encodeToBase64(key: ByteArray, doEncrypt: Boolean = false): String {
        if (doEncrypt) {
            return Base64.encodeToString(cryptoManager.encrypt(key), Base64.DEFAULT)
        }
        return Base64.encodeToString(key, Base64.DEFAULT)
    }

    private fun decodeFromBase64(key: String, isEncrypted: Boolean = false): ByteArray {
        val keyByteArray = Base64.decode(key, Base64.DEFAULT)
        if (isEncrypted) {
            return cryptoManager.decrypt(keyByteArray)
        }
        return keyByteArray
    }
}