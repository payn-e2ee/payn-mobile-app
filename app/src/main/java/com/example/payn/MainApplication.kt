package com.example.payn

import android.app.Application
import com.example.payn.core.data.AppDatabase
import com.example.payn.core.data.CryptoManager
import com.example.payn.core.domain.IdentityKeysEntity
import com.example.payn.di.appModule
import kotlinx.coroutines.runBlocking
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        System.loadLibrary("sqlcipher")

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }

        val koin = GlobalContext.get()

        // Force initialization
        val appDatabase = koin.get<AppDatabase>()
        val cryptoManager = koin.get<CryptoManager>()

        runBlocking {
            val identityKey = appDatabase.identityKeysDao().getIdentityKey()
            if (identityKey == null) {
                val identityKeyPair = cryptoManager.generateX25519KeyPair()
                val privateKey = identityKeyPair.private as X25519PrivateKeyParameters
                val publicKey = identityKeyPair.public as X25519PublicKeyParameters
                val privateKeyEncrypted =
                    cryptoManager.encrypt(privateKey.encoded)
                val privateKeyEncryptedBase64 = android.util.Base64.encodeToString(
                    privateKeyEncrypted,
                    android.util.Base64.DEFAULT
                )
                val publicKeyBase64 = android.util.Base64.encodeToString(
                    publicKey.encoded,
                    android.util.Base64.DEFAULT
                )

                appDatabase.identityKeysDao().insert(
                    IdentityKeysEntity(
                        encryptedPrivateKey = privateKeyEncryptedBase64,
                        publicKey = publicKeyBase64,
                    )
                )
            }
        }
    }
}