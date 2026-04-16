package com.example.payn.core.data

import android.content.Context
import androidx.room.Room
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

class SecureDatabaseFactory(private val cryptoManager: CryptoManager) {
    suspend fun create(context: Context): AppDatabase {
        val passphrase: ByteArray = cryptoManager.getOrCreatePassphrase()
        val factory = SupportOpenHelperFactory(passphrase)
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "secure.db"
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }
}