package com.example.payn.chat.presentation.chat_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import com.example.payn.app.Route
import com.example.payn.chat.data.AudioRecorder
import com.example.payn.chat.presentation.components.ChatDetailHeader
import com.example.payn.chat.presentation.components.MessageInput
import com.example.payn.chat.presentation.components.MessagesBox
import com.example.payn.chat.presentation.components.VideoPlayer
import com.example.payn.ui.theme.Black30
import com.example.payn.ui.theme.Blue400
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Pink400
import com.example.payn.ui.theme.Purple400
import kotlinx.coroutines.launch

@Composable
fun ChatDetailScreen(
    viewModel: ChatDetailViewModel,
    navController: NavHostController,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val user = state.user
    if (user == null) {
        Text("Loading....")
        return
    }

    val audioRecorder = remember { AudioRecorder(context) }

    LaunchedEffect(state.messages) {
        viewModel.markMessagesAsSeen()
    }

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
                viewModel = viewModel,
                messages = state.messages,
                currentUser = viewModel.currentUser!!,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )

            // Message Input
            MessageInput(
                value = state.message,
                onValueChange = { viewModel.setMessage(it) },
                onPickImage = {
                    viewModel.setSelectedImageUri(it)
                    viewModel.setShowSelectedImagePopup(true)
                },
                onPickVideo = {
                    viewModel.setSelectedVideoUri(it)
                    viewModel.setShowSelectedVideoPopup(true)
                },
                onPickFile = { uri ->
                    viewModel.viewModelScope.launch {
                        viewModel.sendFile(
                            fileUri = uri,
                            onCreateChat = {
                                navController.navigate(Route.Chat(it, null)) {
                                    popUpTo<Route.Chat> {
                                        inclusive = true
                                    }
                                }
                            })
                    }
                },
                sendMessage = {
                    viewModel.viewModelScope.launch {
                        viewModel.sendText(
                            content = state.message.toByteArray(),
                            onCreateChat = {
                                navController.navigate(Route.Chat(it, null)) {
                                    popUpTo<Route.Chat> {
                                        inclusive = true
                                    }
                                }
                            },

                            )
                    }
                },
                onStartRecording = {
                    audioRecorder.start()
                },
                onStopRecording = {
                    audioRecorder.stop()
                    viewModel.viewModelScope.launch {
                        viewModel.sendVoice(
                            fileUri = audioRecorder.outputFile?.toUri() ?: return@launch,
                            onCreateChat = {
                                navController.navigate(Route.Chat(it, null)) {
                                    popUpTo<Route.Chat> {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                }
            )
        }
    }

    if (state.showSelectedImagePopup) {
        Dialog(
            onDismissRequest = { viewModel.setShowSelectedImagePopup(false) },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Black30)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.2f))
                        .clickable { viewModel.setShowSelectedImagePopup(false) }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = "Close",
                        tint = White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                AsyncImage(
                    model = state.selectedImageUri,
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Blue500)
                        .clickable {
                            viewModel.viewModelScope.launch {
                                viewModel.sendImage(
                                    onCreateChat = {
                                        navController.navigate(Route.Chat(it, null)) {
                                            popUpTo<Route.Chat> {
                                                inclusive = true
                                            }
                                        }
                                    }
                                )
                                viewModel.setShowSelectedImagePopup(false)
                                viewModel.setSelectedImageUri(null)
                            }
                        }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Send",
                        color = White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    if (state.showSelectedVideoPopup) {
        val selectedVideoUri = state.selectedVideoUri ?: return

        Dialog(
            onDismissRequest = { viewModel.setShowSelectedVideoPopup(false) },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Black30)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    VideoPlayer(
                        videoUri = selectedVideoUri,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    // Bottom Send Button
                    Box(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Blue500)
                            .clickable {
                                viewModel.viewModelScope.launch {
                                    viewModel.sendVideo(
                                        onCreateChat = {
                                            navController.navigate(Route.Chat(it, null)) {
                                                popUpTo<Route.Chat> {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    )
                                    viewModel.setShowSelectedVideoPopup(false)
                                    viewModel.setSelectedVideoUri(null)
                                }
                            }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Send", color = White, fontWeight = FontWeight.SemiBold)
                    }
                }

                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.2f))
                        .clickable { viewModel.setShowSelectedVideoPopup(false) }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = "Close",
                        tint = White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}