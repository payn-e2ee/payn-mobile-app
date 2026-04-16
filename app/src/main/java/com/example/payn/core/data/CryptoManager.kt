package com.example.payn.core.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.coroutines.flow.first
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec


class CryptoManager(private val keyValueStorage: KeyValueStorage) {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val encryptCipher
        get() = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey())
        }

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM, "AndroidKeyStore").apply {
            init(
                KeyGenParameterSpec.Builder(
                    "secret",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .setUserAuthenticationRequired(false)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val iv = cipher.iv
        val encrypted = cipher.doFinal(data)

        // Return IV + ciphertext
        return iv + encrypted
    }

    fun decrypt(data: ByteArray): ByteArray {
        val iv = data.copyOfRange(0, IV_SIZE)
        val encrypted = data.copyOfRange(IV_SIZE, data.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getKey(),
            javax.crypto.spec.GCMParameterSpec(128, iv)
        )

        return cipher.doFinal(encrypted)
    }

    suspend fun getOrCreatePassphrase(): ByteArray {
        val value = keyValueStorage.getString("database_passphrase").first()
        if (value != "") {
            val encrypted =
                android.util.Base64.decode(value, android.util.Base64.DEFAULT)
            return decrypt(encrypted)
        }

        val passphrase = ByteArray(32).apply {
            SecureRandom().nextBytes(this)
        }

        // Encrypt passphrase before storing
        val encrypted = encrypt(passphrase)
        val base64Encrypted =
            android.util.Base64.encodeToString(encrypted, android.util.Base64.DEFAULT)

        keyValueStorage.putString("database_passphrase", base64Encrypted)

        return passphrase
    }

    fun generateX25519KeyPair(): AsymmetricCipherKeyPair {
        val gen = X25519KeyPairGenerator()
        gen.init(X25519KeyGenerationParameters(SecureRandom()))
        return gen.generateKeyPair()
    }

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private const val IV_SIZE = 12
    }

}