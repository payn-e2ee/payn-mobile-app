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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.payn.call.presentation.CallsScreen
import com.example.payn.chat.presentation.ChatsScreen
import com.example.payn.contact.presentation.ContactsScreen
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
        composable<Route.Welcome> {
            WelcomeScreen()
        }
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
    }
}

@Composable
fun App(modifier: Modifier = Modifier) {
    val bottomBarRoutes = setOf(
        Route.Chats,
        Route.Contacts,
        Route.Calls,
        Route.Settings
    )

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val startDestination = Route.Welcome
    var selectedDestination by rememberSaveable(
        stateSaver = RouteSaver
    ) {
        mutableStateOf(startDestination)
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            val showBottomBar = bottomBarRoutes.any { it.toString() == currentRoute }
            if (showBottomBar) {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {

                    NavigationBarItem(
                        selected = selectedDestination == Route.Chats,
                        onClick = {
                            navController.navigate(Route.Chats)
                            selectedDestination = Route.Chats
                        },
                        icon = { Icon(Icons.Default.Email, null) },
                        label = { Text("Chats") }
                    )

                    NavigationBarItem(
                        selected = selectedDestination == Route.Contacts,
                        onClick = {
                            navController.navigate(Route.Contacts)
                            selectedDestination = Route.Contacts
                        },
                        icon = { Icon(Icons.Default.AccountBox, null) },
                        label = { Text("Contact") }
                    )

                    NavigationBarItem(
                        selected = selectedDestination == Route.Calls,
                        onClick = {
                            navController.navigate(Route.Calls)
                            selectedDestination = Route.Calls
                        },
                        icon = { Icon(Icons.Default.Call, null) },
                        label = { Text("Calls") }
                    )

                    NavigationBarItem(
                        selected = selectedDestination == Route.Settings,
                        onClick = {
                            navController.navigate(Route.Settings)
                            selectedDestination = Route.Settings
                        },
                        icon = { Icon(Icons.Default.Settings, null) },
                        label = { Text("Settings") }
                    )
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(
            navController = navController,
            startDestination = Route.Welcome,
            modifier = Modifier.padding(contentPadding)
        )
    }
}