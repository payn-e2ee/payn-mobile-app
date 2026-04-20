package com.example.payn.contact.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircle
import com.example.payn.R
import com.example.payn.contact.domain.Contact
import com.example.payn.core.presentation.components.GlassCard
import com.example.payn.ui.theme.Blue400
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Blue600
import com.example.payn.ui.theme.Gray400
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.Green500
import com.example.payn.ui.theme.White

@Composable
fun ContactItem(
    contact: Contact,
    onContactClick: () -> Unit,
    onChatClick: () -> Unit
) {
    val fullName = "${contact.firstname} ${contact.lastname}"

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onContactClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar with online status
                Box {
                    Image(
                        painter = painterResource(R.drawable.default_profile_picture),
                        contentDescription = "${contact.firstname} ${contact.lastname}",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(2.dp, White.copy(alpha = 0.2f), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    if (true) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(Green500, CircleShape)
                                .border(
                                    2.dp,
                                    if (isSystemInDarkTheme()) Gray900 else White,
                                    CircleShape
                                )
                                .align(Alignment.BottomEnd)
                        )
                    }
                }

                Column {
                    Text(
                        fullName,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSystemInDarkTheme()) White else Gray900,
                        maxLines = 1
                    )
                    Text(
                        contact.contactUser?.phoneNumber ?: "",
                        fontSize = 14.sp,
                        color = if (isSystemInDarkTheme()) Gray400 else Gray600,
                        maxLines = 1
                    )
                }
            }

            IconButton(
                onClick = onChatClick,
                modifier = Modifier
                    .background(Blue500.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    Lucide.MessageCircle,
                    contentDescription = "Message",
                    tint = if (isSystemInDarkTheme()) Blue400 else Blue600,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
