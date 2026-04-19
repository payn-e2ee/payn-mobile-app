package com.example.payn.chat.presentation

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Size
import com.example.payn.R
import com.example.payn.app.Route
import com.example.payn.chat.presentation.components.SearchInput
import com.example.payn.core.presentation.components.GlassCard
import com.example.payn.ui.theme.Blue400
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.Pink400
import com.example.payn.ui.theme.Purple400
import com.example.payn.ui.theme.White
import org.ocpsoft.prettytime.PrettyTime
import java.util.Date
import java.time.Instant


@Composable
fun ChatsScreen(
    viewModel: ListChatsViewModel,
    navController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Blue400, Purple400, Pink400)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Header
            Column {
                Text(
                    text = "Messages",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    text = "Stay connected with everyone",
                    fontSize = 14.sp,
                    color = White.copy(alpha = 0.7f)
                )
            }

            SearchInput(
                value = state.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) }
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.chats, key = { it.id }) { chat ->
                    val otherMember = chat.chatMembers.first { it.user.id != "" }
                    val chatName = "${otherMember.user.firstname} ${otherMember.user.lastname}"
                    val message = chat.messages?.firstOrNull()
                    val messageDelivery = message?.messageDeliveries?.first()

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Route.Chat(chat.id, null))
                            }
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // Avatar
                            Box {
                                // FIXME: Use user profile picture when it available
                                Image(
                                    painter = painterResource(R.drawable.default_profile_picture),
                                    contentDescription = chatName,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                                // FIXME: Add online indicator
//                                if (true) {
//                                    Box(
//                                        modifier = Modifier
//                                            .size(14.dp)
//                                            .align(Alignment.BottomEnd)
//                                            .background(Green500, CircleShape)
//                                    )
//                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Info
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = chatName,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Gray900,
                                        maxLines = 1
                                    )

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        Text(
                                            text =
                                                PrettyTime().format(
                                                    Date.from(
                                                        Instant.parse(chat.createdAt)
                                                    )
                                                ),
                                            fontSize = 12.sp,
                                            color = Gray600
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                if (messageDelivery != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            // FIXME: Decrypt the message before viewing
                                            text = messageDelivery.ciphertext,
                                            fontSize = 14.sp,
                                            color = Gray600,
                                            maxLines = 1
                                        )

                                        when (message.status) {
                                            "delivered" -> {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(start = 8.dp)
                                                        .clip(RoundedCornerShape(50))
                                                        .background(Blue500)
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "✓",
                                                        color = White,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }

                                            "seen" -> {
                                                Text(
                                                    text = "✓✓",
                                                    color = Blue500,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(Blue500),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 28.sp,
                color = White
            )
        }
    }
}