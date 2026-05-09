package com.example.payn.settings.presentation.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pencil
import com.example.payn.R
import com.example.payn.core.domain.models.User
import com.example.payn.core.presentation.components.GlassCard
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Gray400
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.Purple400
import com.example.payn.ui.theme.White
import com.example.payn.ui.theme.White20

@Composable
fun UserProfileHeader(
    user: User?,
    onEdit: () -> Unit,
) {
    val hasAvatar = true
    val isDark = isSystemInDarkTheme()
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                if (hasAvatar) {
                    Image(
                        painter = painterResource(R.drawable.default_profile_picture),
                        contentDescription = "profile image",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, White20, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Blue500, Purple400)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "J",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
                            ),
                            color = White
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Blue500, CircleShape)
                        .clickable {
                            onEdit()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Lucide.Pencil,
                        contentDescription = "Edit Profile",
                        tint = White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${user?.firstname} ${user?.lastname}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isDark) White else Gray900
                )
                Text(
                    text = "@${user?.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) Gray400 else Gray600
                )
                Text(
                    text = "${user?.phoneNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) Gray400 else Gray600
                )
            }
        }
    }
}