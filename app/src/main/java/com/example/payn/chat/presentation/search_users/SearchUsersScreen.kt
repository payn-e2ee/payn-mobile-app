package com.example.payn.chat.presentation.search_users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.example.payn.app.Route
import com.example.payn.ui.theme.Blue400
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Pink400
import com.example.payn.ui.theme.Purple400
import com.example.payn.ui.theme.White

@Composable
fun SearchUsersScreen(
    viewModel: SearchUsersViewModel,
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Blue400, Purple400, Pink400),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp),
        ) {
            RowHeader(
                query = state.query,
                onBack = { navController.popBackStack() },
            )

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = White)
                    }
                }

                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = state.error!!, color = White)
                    }
                }

                state.users.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "No users found", color = White.copy(alpha = 0.8f))
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
                    ) {
                        items(state.users, key = { it.id }) { user ->
                            SearchUserItem(
                                user = user,
                                isAddingContact = state.addingContactUserId == user.id,
                                onOpenChat = {
                                    val chatId = user.chatMembers.firstOrNull()?.chatId
                                    if (chatId == null) {
                                        navController.navigate(
                                            Route.Chat(id = null, userId = user.id),
                                        )
                                    } else {
                                        navController.navigate(
                                            Route.Chat(id = chatId, userId = null),
                                        )
                                    }
                                },
                                onAddContact = { viewModel.addContact(user) },
                            )
                        }
                    }
                }
            }

            state.addContactError?.let { error ->
                Text(
                    text = error,
                    color = White,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun RowHeader(
    query: String,
    onBack: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Lucide.ArrowLeft,
                contentDescription = "Back",
                tint = White,
            )
        }
        Column {
            Text(
                text = "Search users",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = White,
            )
            Text(
                text = "\"$query\"",
                fontSize = 14.sp,
                color = Gray600,
            )
        }
    }
}
