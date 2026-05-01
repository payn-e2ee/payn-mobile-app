package com.example.payn.app

import kotlinx.serialization.Serializable
import androidx.compose.runtime.saveable.Saver
import java.net.URI

@Serializable
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
    data class Contact(val id: String) : Route

    @Serializable
    data object Calls : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object Register : Route

    @Serializable
    data object Notifications : Route
}

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
            is Route.Contact -> "contact/${route.id}"
            is Route.Notifications -> "notifications"
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
            "notifications" -> Route.Notifications
            else -> {
                val uri = URI(value)
                val pathSegments = uri.path.split("/").filter { it.isNotEmpty() }
                val params = parseQueryParams(uri.query ?: "")

                when (pathSegments.getOrNull(0)) {
                    "contact" -> {
                        val id = pathSegments.getOrNull(1) ?: ""
                        Route.Contact(id)
                    }
                    "chat" -> Route.Chat(pathSegments.getOrNull(1), params["user_id"])
                    else -> Route.Welcome
                }
            }
        }
    }
)

fun parseQueryParams(query: String): Map<String, String> =
    if (query.isEmpty()) emptyMap() else query.split("&").mapNotNull { 
        it.split("=").takeIf { it.size == 2 }?.let { (k, v) -> k to v } 
    }.toMap()