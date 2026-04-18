package com.example.payn.app

import kotlinx.serialization.Serializable
import androidx.compose.runtime.saveable.Saver

val RouteSaver = Saver<Route, String>(
    save = { route ->
        when (route) {
            Route.Welcome -> "welcome"
            Route.Chats -> "chats"
            Route.Contacts -> "contacts"
            Route.Calls -> "calls"
            Route.Settings -> "settings"
            Route.Login -> "login"
        }
    },
    restore = { value ->
        when (value) {
            "welcome" -> Route.Welcome
            "chats" -> Route.Chats
            "contacts" -> Route.Contacts
            "calls" -> Route.Calls
            "settings" -> Route.Settings
            "login" -> Route.Login
            else -> Route.Welcome
        }
    }
)

sealed interface Route {
    @Serializable
    data object Welcome : Route

    @Serializable
    data object Chats : Route

    @Serializable
    data object Contacts : Route

    @Serializable
    data object Calls : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Login : Route
}