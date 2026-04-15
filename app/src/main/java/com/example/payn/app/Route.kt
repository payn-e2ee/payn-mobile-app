package com.example.payn.app

import kotlinx.serialization.Serializable
import androidx.compose.runtime.saveable.Saver

val RouteSaver = Saver<Route, String>(
    save = { route ->
        when (route) {
            Route.Home -> "home"
            Route.Chats -> "chats"
            Route.Contacts -> "contacts"
            Route.Calls -> "calls"
            Route.Settings -> "settings"
        }
    },
    restore = { value ->
        when (value) {
            "home" -> Route.Home
            "chats" -> Route.Chats
            "contacts" -> Route.Contacts
            "calls" -> Route.Calls
            "settings" -> Route.Settings
            else -> Route.Home
        }
    }
)

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object Chats : Route

    @Serializable
    data object Contacts : Route

    @Serializable
    data object Calls : Route

    @Serializable
    data object Settings : Route
}