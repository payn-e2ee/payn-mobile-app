package com.example.payn.di

import com.example.payn.auth.data.network.AuthDataSource
import com.example.payn.auth.data.repository.AuthRepository
import com.example.payn.auth.presentation.login.LoginViewModel
import com.example.payn.core.data.CryptoManager
import com.example.payn.core.data.HttpClientFactory
import com.example.payn.core.data.KeyValueStorage
import com.example.payn.core.data.SecureDatabaseFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<HttpClientEngine> { OkHttp.create() }
    single { HttpClientFactory.create(get()) }
    singleOf(::CryptoManager)
    singleOf(::SecureDatabaseFactory)
    singleOf(::AuthDataSource)
    singleOf(::AuthRepository)
    single {
        runBlocking {
            get<SecureDatabaseFactory>().create(androidApplication())
        }
    }
    single {
        KeyValueStorage(androidApplication())
    }

    viewModelOf(::LoginViewModel)
}