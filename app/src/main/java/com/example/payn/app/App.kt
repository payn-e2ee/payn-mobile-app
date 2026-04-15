package com.example.payn.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.payn.call.presentation.CallsScreen
import com.example.payn.chat.presentation.ChatsScreen
import com.example.payn.contact.presentation.ContactsScreen
import com.example.payn.home.presentation.HomeScreen
import com.example.payn.profile.presentation.ProfileScreen
import com.example.payn.settings.presentation.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Route = Route.Chats,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination
    ) {
        composable<Route.Chats> {
            ChatsScreen()
        }

        composable<Route.Contacts> {
            ContactsScreen()
        }

        composable<Route.Calls> {
            CallsScreen()
        }

        composable<Route.Settings> {
            SettingsScreen()
        }

        composable<Route.Profile> {
            ProfileScreen("Abdelfetah")
        }
    }
}

@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Route.Chats
    var selectedDestination by rememberSaveable(
        stateSaver = RouteSaver
    ) {
        mutableStateOf<Route>(startDestination)
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                NavigationBarItem(
                    selected = selectedDestination == Route.Chats,
                    onClick = {
                        navController.navigate(route = Route.Chats)
                        selectedDestination = Route.Chats
                    },
                    icon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Chat icon"
                        )
                    },
                    label = { Text("Chats") }
                )
                NavigationBarItem(
                    selected = selectedDestination == Route.Contacts,
                    onClick = {
                        navController.navigate(route = Route.Contacts)
                        selectedDestination = Route.Contacts
                    },
                    icon = {
                        Icon(
                            Icons.Default.AccountBox,
                            contentDescription = "contacts icon"
                        )
                    },
                    label = { Text("Contact") }
                )
                NavigationBarItem(
                    selected = selectedDestination == Route.Calls,
                    onClick = {
                        navController.navigate(route = Route.Calls)
                        selectedDestination = Route.Calls
                    },
                    icon = {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Call icon"
                        )
                    },
                    label = { Text("calls") }
                )
                NavigationBarItem(
                    selected = selectedDestination == Route.Settings,
                    onClick = {
                        navController.navigate(route = Route.Settings)
                        selectedDestination = Route.Settings
                    },
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings icon"
                        )
                    },
                    label = { Text("Settings") }
                )
            }
        }
    ) { contentPadding ->
        AppNavHost(
            navController = navController,
            startDestination = Route.Chats,
            modifier = Modifier.padding(contentPadding)
        )
    }
}