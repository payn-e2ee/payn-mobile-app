package com.example.payn.chat.presentation.search_users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.UserPlus
import com.example.payn.core.data.dto.SearchUserDTO
import com.example.payn.core.presentation.components.GlassCard
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Blue600
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.Purple400
import com.example.payn.ui.theme.White

@Composable
fun SearchUserItem(
    user: SearchUserDTO,
    isAddingContact: Boolean,
    onOpenChat: () -> Unit,
    onAddContact: () -> Unit,
) {
    val fullName = listOfNotNull(user.firstname, user.lastname)
        .joinToString(" ")
        .ifBlank { user.username }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Blue500, Purple400),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = fullName.take(1),
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                }

                Column {
                    Text(
                        text = fullName,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray900,
                        maxLines = 1,
                    )
                    Text(
                        text = "@${user.username}",
                        fontSize = 14.sp,
                        color = Gray600,
                        maxLines = 1,
                    )
                    Text(
                        text = user.phoneNumber,
                        fontSize = 14.sp,
                        color = Gray600,
                        maxLines = 1,
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!user.isContact) {
                    TextButton(
                        onClick = onAddContact,
                        enabled = !isAddingContact,
                    ) {
                        Icon(
                            Lucide.UserPlus,
                            contentDescription = "Add contact",
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                IconButton(
                    onClick = onOpenChat,
                    modifier = Modifier.background(Blue500.copy(alpha = 0.2f), CircleShape),
                ) {
                    Icon(
                        Lucide.MessageCircle,
                        contentDescription = "Open chat",
                        tint = Blue600,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
