package com.example.payn.chat.presentation.chat_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.payn.chat.domain.Message
import com.example.payn.chat.presentation.components.ChatDetailHeader
import com.example.payn.chat.presentation.components.MessageInput
import com.example.payn.chat.presentation.components.MessagesBox
import com.example.payn.ui.theme.Blue400
import com.example.payn.ui.theme.Pink400
import com.example.payn.ui.theme.Purple400

@Composable
fun ChatDetailScreen(
    viewModel: ChatDetailViewModel,
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val user = state.user

    if (user == null) {
        Text("Loading....")
        return
    }

    var message by remember { mutableStateOf("") }
    val messages = emptyList<Message>()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(listOf(Blue400, Purple400, Pink400))
            )
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            ChatDetailHeader(navController, user)

            // Messages
            MessagesBox(
                messages = messages,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )

            // Message Input
            MessageInput(message, { message = it })
        }
    }
}