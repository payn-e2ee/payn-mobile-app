package com.example.payn.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.Volume2
import com.example.payn.core.presentation.components.GlassCard
import com.example.payn.ui.theme.Blue400
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Blue600
import com.example.payn.ui.theme.Blue900
import com.example.payn.ui.theme.Gray200
import com.example.payn.ui.theme.Gray400
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.Pink400
import com.example.payn.ui.theme.Pink900
import com.example.payn.ui.theme.Purple400
import com.example.payn.ui.theme.Purple900
import com.example.payn.ui.theme.White

import androidx.compose.runtime.collectAsState

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    navController: NavHostController
) {
    val messageNotificationsEnabled by viewModel.messageNotificationsEnabled.collectAsState()
    val notificationSoundEnabled by viewModel.notificationSoundEnabled.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()

    val backgroundGradient = if (isSystemInDarkTheme()) {
        Brush.linearGradient(listOf(Blue900, Purple900, Pink900))
    } else {
        Brush.linearGradient(listOf(Blue400, Purple400, Pink400))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Lucide.ArrowLeft,
                        contentDescription = "Back",
                        tint = White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Notifications",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = "Manage your notification preferences",
                        fontSize = 14.sp,
                        color = White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info Card
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Blue500.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Bell,
                            contentDescription = null,
                            tint = if (isSystemInDarkTheme()) Blue400 else Blue600,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "You can customize notification settings for individual chats from their info pages.",
                        fontSize = 14.sp,
                        color = if (isSystemInDarkTheme()) Gray400 else Gray600,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Message Notifications
            NotificationSettingItem(
                title = "Message Notifications",
                subtitle = "Receive notifications for new messages",
                icon = Lucide.MessageCircle,
                iconColor = Blue500,
                isEnabled = messageNotificationsEnabled,
                onCheckedChange = { viewModel.setMessageNotificationsEnabled(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sound & Vibration Section
            Text(
                text = "Sound & Vibration",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            NotificationSettingItem(
                title = "Notification Sound",
                subtitle = "Play sound for notifications",
                icon = Lucide.Volume2,
                iconColor = Blue500,
                isEnabled = notificationSoundEnabled,
                onCheckedChange = { viewModel.setNotificationSoundEnabled(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            NotificationSettingItem(
                title = "Vibration",
                subtitle = "Vibrate for notifications",
                icon = Lucide.Bell,
                iconColor = Blue500,
                isEnabled = vibrationEnabled,
                onCheckedChange = { viewModel.setVibrationEnabled(it) }
            )

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun NotificationSettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    isEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSystemInDarkTheme()) iconColor.copy(alpha = 0.8f) else iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSystemInDarkTheme()) White else Gray900
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = if (isSystemInDarkTheme()) Gray400 else Gray600
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = White,
                    checkedTrackColor = Blue500,
                    uncheckedThumbColor = Gray400,
                    uncheckedTrackColor = Gray200.copy(alpha = 0.5f)
                )
            )
        }
    }
}
