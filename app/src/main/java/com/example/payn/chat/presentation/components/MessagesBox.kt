package com.example.payn.chat.presentation.components

import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import com.composables.icons.lucide.Download
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import com.example.payn.chat.domain.ChatMessage
import com.example.payn.chat.domain.MessageType
import com.example.payn.chat.presentation.chat_detail.ChatDetailViewModel
import com.example.payn.core.data.mappers.toUri
import com.example.payn.core.domain.models.User
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun MessagesBox(
    viewModel: ChatDetailViewModel,
    messages: List<ChatMessage>,
    currentUser: User,
    modifier: Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                // In reverseLayout, the "bottom" of the list is 0
                listState.animateScrollToItem(0)
            }
        }
    }

    val threshold = 1 // Number of items from the top to trigger fetch

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            // In reverseLayout, the "top" of the list is the last index
            lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - threshold
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && viewModel.chatId != null) {
            viewModel.fetchMessages()
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        reverseLayout = true,
    ) {
        items(messages, key = { it.id }) { message ->
            val isMe = message.senderUserId == currentUser.id
            var isLoading by remember { mutableStateOf(true) }
            var content by remember { mutableStateOf("".toByteArray()) }

            LaunchedEffect(message) {
                content = when (message.messageType) {
                    MessageType.TEXT ->
                        viewModel.decryptMessage(
                            messageId = message.id,
                            ciphertext = Base64.decode(message.ciphertext, Base64.DEFAULT),
                            ephemeralPublicKey = message.ephemeralPublicKey,
                            messageCounter = message.messageCounter,
                            senderDeviceId = message.senderDeviceId,
                            receiptDeviceId = message.recipientDeviceId,
                        )

                    MessageType.IMAGE, MessageType.VOICE, MessageType.VIDEO -> viewModel.decryptMessage(
                        messageId = message.id,
                        ciphertext = viewModel.getAttachmentFileBytes(message.attachment!!.id),
                        ephemeralPublicKey = message.ephemeralPublicKey,
                        messageCounter = message.messageCounter,
                        senderDeviceId = message.senderDeviceId,
                        receiptDeviceId = message.recipientDeviceId,
                    )

                    else -> "".toByteArray()
                }
                isLoading = false
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
            ) {
                when (message.messageType) {
                    MessageType.TEXT -> TextMessage(
                        content = content,
                        status = message.status,
                        isMe = isMe,
                        createdAt = message.createdAt,
                    )

                    MessageType.IMAGE -> ImageMessage(
                        content = content,
                        isMe = isMe,
                        createdAt = message.createdAt,
                        isLoading = isLoading,
                        showImageOnFullScreen = { viewModel.showImageOnFullScreen(it) },
                        status = message.status,
                    )

                    MessageType.VOICE -> VoiceMessage(
                        content = content,
                        status = message.status,
                        isMe = isMe,
                        createdAt = message.createdAt,
                        isLoading = isLoading,
                    )

                    MessageType.FILE -> FileMessage(
                        fileName = message.attachment?.originalFileName
                            ?: "Unknown file", // Extract from message content
                        fileSize = message.attachment?.originalFileSize?.toString()
                            ?: "Unknown size",      // Extract from message metadata
                        status = message.status,
                        isMe = isMe,
                        createdAt = message.createdAt,
                        onDownloadClick = {
                            viewModel.viewModelScope.launch {
                                viewModel.downloadFile(context, message)
                            }
                        }
                    )

                    MessageType.VIDEO -> VideoMessage(
                        videoUri = if (isLoading) null else content.toUri(
                            context,
                            message.attachment?.originalFileName ?: "video"
                        ),
                        status = message.status,
                        isMe = isMe,
                        createdAt = message.createdAt,
                        isLoading = isLoading,
                        showVideoOnFullScreen = { viewModel.showVideoOnFullScreen(it) }
                    )
                }
            }
        }
    }

    if (state.isFullScreenOpen) {
        Dialog(
            onDismissRequest = { viewModel.closeFullScreen() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                if (state.selectedImageUri != null) {
                    AsyncImage(
                        model = state.selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else if (state.selectedVideoUri != null) {
                    VideoPlayer(
                        videoUri = state.selectedVideoUri!!,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Probably Unhandled state")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Close Button
                    IconButton(
                        onClick = { viewModel.closeFullScreen() },
                        modifier = Modifier.background(
                            Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        )
                    ) {
                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            if (state.selectedImageUri != null) {
                                viewModel.saveImageToGallery(state.selectedImageUri!!)
                            } else if (state.selectedVideoUri != null) {
                                viewModel.saveVideoToGallery(state.selectedVideoUri!!)
                            }
                        },
                        modifier = Modifier.background(
                            Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        )
                    ) {
                        Icon(
                            imageVector = Lucide.Download,
                            contentDescription = "Download",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

fun formatIsoDate(isoString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val outputFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }

        val date = inputFormat.parse(isoString)
        date?.let { outputFormat.format(it) } ?: ""
    } catch (e: Exception) {
        ""
    }
}