package com.example.payn.core.data

import android.content.Context

class DatabaseProvider(val secureDatabaseFactory: SecureDatabaseFactory) {

    private var _appDatabase: AppDatabase? = null

    val appDatabase: AppDatabase
        get() = _appDatabase
            ?: throw IllegalStateException("Database not initialized")

    suspend fun createOrSwitchDatabase(context: Context, name: String) {
        _appDatabase?.close()
        _appDatabase = secureDatabaseFactory.create(context, name)
    }
}