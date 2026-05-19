package com.example.payn.core.data

import android.content.Context

class DatabaseProvider(val secureDatabaseFactory: SecureDatabaseFactory) {
    var appDatabase: AppDatabase? = null

    suspend fun createOrSwitchDatabase(context: Context, name: String) {
        appDatabase?.close()
        appDatabase = secureDatabaseFactory.create(context, name)
    }
}