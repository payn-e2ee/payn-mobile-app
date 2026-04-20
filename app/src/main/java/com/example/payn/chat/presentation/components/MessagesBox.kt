package com.example.payn.chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.payn.chat.domain.Message
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.White


@Composable
fun MessagesBox(
    messages: List<Message>,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(messages, key = { it.id }) { msg ->
            val messageDelivery = msg.messageDeliveries.first()
            val isMe = msg.userId == "me"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
            ) {

                Box(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .then(
                            if (isMe) {
                                Modifier.background(Blue500)
                            } else {
                                Modifier
                                    .background(White.copy(alpha = 0.7f))
                            }
                        )
                        .padding(12.dp)
                ) {

                    Column {
                        Text(
                            text = messageDelivery.ciphertext,
                            color = if (isMe) White else Gray900
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = messageDelivery.createdAt,
                                fontSize = 10.sp,
                                color = if (isMe) White.copy(alpha = 0.7f) else Gray600
                            )

                            if (isMe) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (true) "✓✓" else "✓",
                                    fontSize = 10.sp,
                                    color = White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}