package com.example.payn.di

import com.example.payn.core.data.CryptoManager
import com.example.payn.core.data.HttpClientFactory
import com.example.payn.core.data.KeyValueStorage
import com.example.payn.core.data.SecureDatabaseFactory
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::CryptoManager)
    singleOf(::SecureDatabaseFactory)
    single { HttpClientFactory.create(get()) }
    single {
        runBlocking {
            get<SecureDatabaseFactory>().create(androidApplication())
        }
    }
    single {
        KeyValueStorage(androidApplication())
    }
}