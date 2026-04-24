package com.example.payn.app

import kotlinx.serialization.Serializable
import androidx.compose.runtime.saveable.Saver
import java.net.URI


val RouteSaver = Saver<Route, String>(
    save = { route ->
        when (route) {
            is Route.Welcome -> "welcome"
            is Route.Chats -> "chats"
            is Route.Contacts -> "contacts"
            is Route.Calls -> "calls"
            is Route.Settings -> "settings"
            is Route.Login -> "login"
            is Route.Chat -> if (route.id != null) "chat/${route.id}" else "chat/init?user_id=${route.userId}"
            is Route.Register -> "register"
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
            "register" -> Route.Register
            else -> {
                val uri = URI(value)
                val pathSegments = uri.path.split("/")
                val params = parseQueryParams(uri.query)

                when (pathSegments.first()) {
                    "chat" -> Route.Chat(pathSegments.getOrNull(1), params["user_id"])
                    else -> Route.Welcome
                }
            }
        }
    }
)

fun parseQueryParams(query: String): Map<String, String> =
    query.split("&").mapNotNull { it.split("=").takeIf { it.size == 2 }?.let { (k, v) -> k to v } }
        .toMap()

sealed interface Route {
    @Serializable
    data object Welcome : Route

    @Serializable
    data object Chats : Route

    @Serializable
    data class Chat(val id: String?, val userId: String?) : Route

    @Serializable
    data object Contacts : Route

    @Serializable
    data object Calls : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object Register : Route
}