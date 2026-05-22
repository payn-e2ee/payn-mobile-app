package com.example.payn.di

import com.example.payn.auth.data.network.AuthDataSource
import com.example.payn.auth.data.repository.AuthRepository
import com.example.payn.auth.presentation.login.LoginViewModel
import com.example.payn.auth.presentation.register.RegisterViewModel
import com.example.payn.auth.presentation.welcome.WelcomeViewModel
import com.example.payn.chat.data.network.ChatDataSource
import com.example.payn.chat.data.repository.ChatRepository
import com.example.payn.chat.data.security.DoubleRatchetEngine
import com.example.payn.chat.data.service.ChatService
import com.example.payn.chat.presentation.chat_list.ChatListViewModel
import com.example.payn.chat.presentation.chat_detail.ChatDetailViewModel
import com.example.payn.contact.data.network.ContactDataSource
import com.example.payn.contact.data.repository.ContactRepository
import com.example.payn.contact.presentation.contact_detail.ContactDetailViewModel
import com.example.payn.contact.presentation.contact_list.ListContactsViewModel
import com.example.payn.core.data.AuthSessionManager
import com.example.payn.settings.presentation.SettingsViewModel
import com.example.payn.core.data.CryptoManager
import com.example.payn.core.data.DatabaseProvider
import com.example.payn.core.data.FileManager
import com.example.payn.core.data.HttpClientFactory
import com.example.payn.core.data.KeyValueStorage
import com.example.payn.core.data.SecureDatabaseFactory
import com.example.payn.core.data.network.AttachmentDataSource
import com.example.payn.core.data.network.MqttWebSocketClient
import com.example.payn.core.data.network.UserDataSource
import com.example.payn.core.data.repository.AttachmentRepository
import com.example.payn.core.data.repository.UserRepository
import com.example.payn.core.data.PaynNotificationManager
import com.example.payn.settings.presentation.NotificationsViewModel
import com.example.payn.settings.presentation.edit_profile.EditProfileViewModel
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
    singleOf(::DatabaseProvider)
    singleOf(::MqttWebSocketClient)

    singleOf(::AuthDataSource)
    singleOf(::ChatDataSource)
    singleOf(::UserDataSource)
    singleOf(::ContactDataSource)
    singleOf(::AttachmentDataSource)

    singleOf(::AuthRepository)
    singleOf(::ChatRepository)
    singleOf(::UserRepository)
    singleOf(::ContactRepository)
    singleOf(::AttachmentRepository)

    singleOf(::DoubleRatchetEngine)
    singleOf(::ChatService)
    singleOf(::AuthSessionManager)
    single {
        KeyValueStorage(androidApplication())
    }
    single {
        PaynNotificationManager(androidApplication(), get())
    }
    single { FileManager(androidApplication()) }

    viewModelOf(::WelcomeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::ChatListViewModel)
    viewModelOf(::ChatDetailViewModel)
    viewModelOf(::ListContactsViewModel)
    viewModelOf(::ContactDetailViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::EditProfileViewModel)
    viewModelOf(::NotificationsViewModel)
}