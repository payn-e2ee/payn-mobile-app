package com.example.payn.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.payn.app.components.BottomNav
import com.example.payn.auth.presentation.login.LoginScreen
import com.example.payn.auth.presentation.login.LoginViewModel
import com.example.payn.auth.presentation.register.RegisterScreen
import com.example.payn.auth.presentation.register.RegisterViewModel
import com.example.payn.auth.presentation.welcome.WelcomeScreen
import com.example.payn.auth.presentation.welcome.WelcomeViewModel
import com.example.payn.call.presentation.CallsScreen
import com.example.payn.contact.presentation.contact_detail.ContactDetailScreen
import com.example.payn.contact.presentation.contact_detail.ContactDetailViewModel
import com.example.payn.chat.presentation.chat_list.ChatListScreen
import com.example.payn.chat.presentation.chat_list.ChatListViewModel
import com.example.payn.chat.presentation.chat_detail.ChatDetailScreen
import com.example.payn.chat.presentation.chat_detail.ChatDetailViewModel
import com.example.payn.contact.presentation.contact_list.ContactsScreen
import com.example.payn.contact.presentation.contact_list.ListContactsViewModel
import com.example.payn.settings.presentation.SettingsScreen
import com.example.payn.settings.presentation.SettingsViewModel
import com.example.payn.settings.presentation.NotificationsScreen
import com.example.payn.settings.presentation.edit_profile.EditProfileScreen
import com.example.payn.settings.presentation.edit_profile.EditProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

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
            val viewModel = koinViewModel<WelcomeViewModel>()

            WelcomeScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable<Route.Login> {
            val viewModel = koinViewModel<LoginViewModel>()

            LoginScreen(
                viewModel = viewModel,
                navController
            )
        }

        composable<Route.Register> {
            val viewModel = koinViewModel<RegisterViewModel>()

            RegisterScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable<Route.Chats> {
            val viewModel = koinViewModel<ChatListViewModel>()
            ChatListScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable<Route.Chat> {
            val viewModel = koinViewModel<ChatDetailViewModel>()
            ChatDetailScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable<Route.Contacts> {
            val viewModel = koinViewModel<ListContactsViewModel>()
            ContactsScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable<Route.Contact> {
            val viewModel = koinViewModel<ContactDetailViewModel>()
            ContactDetailScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable<Route.Calls> {
            CallsScreen()
        }

        composable<Route.Settings> {
            val viewModel = koinViewModel<SettingsViewModel>()
            SettingsScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable<Route.Notifications> {
            NotificationsScreen(
                navController = navController
            )
        }

        composable<Route.EditProfile> {
            val viewModel = koinViewModel<EditProfileViewModel>()
            EditProfileScreen(
                viewModel = viewModel,
                navController = navController
            )
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

    // Track selected destination
    var selectedDestination by rememberSaveable(stateSaver = RouteSaver) {
        mutableStateOf<Route>(Route.Chats)
    }

    Scaffold(
        modifier = modifier,
        // Set containerColor to Transparent so the screen's custom background gradients show
        containerColor = Color.Transparent,
        bottomBar = {
            val showBottomBar = bottomBarRoutes.any { it::class.qualifiedName == currentRoute }

            if (showBottomBar) {
                BottomNav(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Standard BottomNav behavior: pop up to start to avoid stack accumulation
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        selectedDestination = route
                    }
                )
            }
        }
    ) { contentPadding ->
        AppNavHost(
            navController = navController,
            startDestination = Route.Welcome,
            // Pass contentPadding but note that our BottomNav is floating
            modifier = Modifier.padding(contentPadding)
        )
    }
}
