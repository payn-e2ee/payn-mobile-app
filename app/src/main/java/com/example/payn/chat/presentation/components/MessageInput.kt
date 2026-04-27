package com.example.payn.chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mic
import com.composables.icons.lucide.Paperclip
import com.example.payn.core.presentation.components.GlassCard
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.White
import com.example.payn.ui.theme.White30

@Preview
@Composable
fun MessageInputPreview() {
    MessageInput(
        value = "",
        onValueChange = {},
        sendMessage = {}
    )
}

@Composable
fun MessageInput(
    value: String,
    onValueChange: (value: String) -> Unit,
    sendMessage: () -> Unit
) {
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
                Icon(
                    imageVector = Lucide.Paperclip,
                    contentDescription = "paper-clip"
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    shape = RoundedCornerShape(16.dp),
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = { Text("Type a message...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = White30
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (value.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Blue500)
                            .clickable {
                                sendMessage()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("➤", color = White)
                    }
                } else {
                    Icon(
                        imageVector = Lucide.Mic,
                        contentDescription = "mic",
                    )
                }
            }
        }
    }
}