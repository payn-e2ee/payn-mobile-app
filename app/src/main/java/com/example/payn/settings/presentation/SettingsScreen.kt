package com.example.payn.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.example.payn.app.Route
import com.example.payn.core.presentation.components.GlassCard
import com.example.payn.settings.presentation.components.UserProfileHeader
import com.example.payn.ui.theme.Blue400
import com.example.payn.ui.theme.Pink400
import com.example.payn.ui.theme.Purple400
import com.example.payn.ui.theme.Red500
import com.example.payn.ui.theme.White

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navController: NavHostController
) {
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
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {

            // Header
            Column {
                Text(
                    text = "Settings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    text = "Manage your account",
                    fontSize = 14.sp,
                    color = White.copy(alpha = 0.7f)
                )
            }

            UserProfileHeader(
                user = viewModel.currentUser,
                onEdit = { navController.navigate(Route.EditProfile) }
            )

            // Logout Button
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.logout {
                        navController.navigate(Route.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Red500.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.LogOut,
                            contentDescription = "Log Out",
                            tint = Red500,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Log Out",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Red500
                    )
                }
            }
        }
    }
}
