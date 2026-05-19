package com.example.payn

import android.app.Application
import com.example.payn.core.data.AuthSessionManager
import com.example.payn.core.data.KeyValueStorage
import com.example.payn.di.appModule
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestPipeline
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
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

        val httpClient = koin.get<HttpClient>()
        val keyValueStorage = koin.get<KeyValueStorage>()
        val authSessionManager = koin.get<AuthSessionManager>()

        httpClient.requestPipeline.intercept(HttpRequestPipeline.State) {
            val accessToken = keyValueStorage.getString("access_token").firstOrNull()
            if (accessToken != null) {
                context.headers.append("Authorization", "Bearer $accessToken")
            }

            proceed()
        }

        runBlocking {
            authSessionManager.initializeSession()
        }
    }
}