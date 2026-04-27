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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.payn.chat.domain.ChatMessage
import com.example.payn.chat.presentation.chat_detail.ChatDetailViewModel
import com.example.payn.core.domain.models.User
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.White
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
            val isMe = message.userId == currentUser.id

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
                            text = message.content,
                            color = if (isMe) White else Gray900
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = formatIsoDate(message.createdAt),
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