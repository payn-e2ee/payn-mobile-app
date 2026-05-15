package com.example.payn.chat.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Image
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mic
import com.composables.icons.lucide.Paperclip
import com.example.payn.core.presentation.components.GlassCard
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.White
import com.example.payn.ui.theme.White30
import kotlinx.coroutines.delay

@Composable
fun MessageInput(
    value: String,
    onValueChange: (value: String) -> Unit,
    onPickImage: (uri: Uri) -> Unit,
    onPickVideo: (uri: Uri) -> Unit,
    onPickFile: (uri: Uri) -> Unit,
    sendMessage: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableLongStateOf(0L) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val mimeType = context.contentResolver.getType(it)
                when {
                    mimeType?.startsWith("image/") == true -> {
                        onPickImage(uri)
                    }

                    mimeType?.startsWith("video/") == true -> {
                        onPickVideo(uri)
                    }
                }
            }
        }
    )

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> if (uri != null) onPickFile(uri) }
    )

    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingTime = 0L
            while (isRecording) {
                delay(1000L)
                recordingTime++
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        GlassCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isRecording) {
                    Icon(
                        imageVector = Lucide.Paperclip,
                        contentDescription = "file",
                        modifier = Modifier.clickable { filePickerLauncher.launch("*/*") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Lucide.Image,
                        contentDescription = "image",
                        modifier = Modifier.clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Box(modifier = Modifier.weight(1f)) {
                    if (isRecording) {
                        val minutes = recordingTime / 60
                        val seconds = recordingTime % 60
                        Text(
                            text = String.format("Recording %02d:%02d", minutes, seconds),
                            color = Color.Red,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        TextField(
                            value = value,
                            onValueChange = onValueChange,
                            placeholder = { Text("Type a message...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = White30
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (value.isNotBlank() && !isRecording) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Blue500)
                            .clickable { sendMessage() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("➤", color = White)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) Color.Red.copy(alpha = 0.1f) else Color.Transparent)
                            .clickable {
                                if (isRecording) {
                                    onStopRecording()
                                    isRecording = false
                                } else {
                                    onStartRecording()
                                    isRecording = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Mic,
                            contentDescription = "mic",
                            tint = if (isRecording) Color.Red else Blue500
                        )
                    }
                }
            }
        }
    }
}