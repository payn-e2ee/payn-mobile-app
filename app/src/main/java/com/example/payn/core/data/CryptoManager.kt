package com.example.payn.core.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.coroutines.flow.first
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.agreement.X25519Agreement
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters
import org.bouncycastle.crypto.generators.HKDFBytesGenerator
import org.bouncycastle.crypto.params.HKDFParameters
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

    /**
     * Derives a cryptographically strong key from an input key (Shared Secret or Master Key).
     *
     * @param inputKey The source material (e.g., result of generateSharedSecret).
     * @param salt Optional random salt.
     * @param info Optional context info to ensure domain separation.
     * @param outputLength Length of the desired key (default 32 bytes for AES-256).
     */
    fun deriveKeyViaHKDF(
        inputKey: ByteArray,
        salt: ByteArray? = null,
        info: ByteArray? = null,
        outputLength: Int = 32
    ): ByteArray {
        val hkdf = HKDFBytesGenerator(SHA256Digest())
        val params = HKDFParameters(inputKey, salt, info)

        hkdf.init(params)

        val generatedKey = ByteArray(outputLength)
        hkdf.generateBytes(generatedKey, 0, outputLength)

        return generatedKey
    }

    fun generateSharedSecret(
        privateKey: X25519PrivateKeyParameters,
        publicKey: X25519PublicKeyParameters
    ): ByteArray {
        val agreement = X25519Agreement()
        agreement.init(privateKey)

        val sharedSecret = ByteArray(agreement.agreementSize)
        agreement.calculateAgreement(publicKey, sharedSecret, 0)

        return sharedSecret
    }

    fun encryptWithKey(data: ByteArray, key: ByteArray): ByteArray {
        // Convert the raw ByteArray into a SecretKeySpec
        val secretKey = javax.crypto.spec.SecretKeySpec(key, "AES")

        val iv = ByteArray(IV_SIZE)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            secretKey,
            javax.crypto.spec.GCMParameterSpec(TAG_SIZE, iv)
        )

        val encrypted = cipher.doFinal(data)

        // Return IV + ciphertext (IV_SIZE is 12 for GCM)
        return iv + encrypted
    }

    fun decryptWithKey(data: ByteArray, key: ByteArray): ByteArray {
        val secretKey = javax.crypto.spec.SecretKeySpec(key, "AES")

        val iv = data.copyOfRange(0, IV_SIZE)
        val encrypted = data.copyOfRange(IV_SIZE, data.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            javax.crypto.spec.GCMParameterSpec(TAG_SIZE, iv)
        )

        return cipher.doFinal(encrypted)
    }

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private const val IV_SIZE = 12
        private const val TAG_SIZE = 128
    }

}