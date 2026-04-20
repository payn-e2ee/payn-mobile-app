package com.example.payn.app.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.example.payn.app.Route
import com.example.payn.ui.theme.*

data class NavItem(
    val route: Route,
    val icon: ImageVector, // Changed from LucideIcon to ImageVector
    val label: String
)

val navItems = listOf(
    NavItem(Route.Chats, Lucide.MessageCircle, "Chats"),
    NavItem(Route.Contacts, Lucide.Users, "Contacts"),
    NavItem(Route.Calls, Lucide.Phone, "Calls"),
    NavItem(Route.Settings, Lucide.Settings, "Settings")
)

@Composable
fun BottomNav(
    currentRoute: String?,
    onNavigate: (Route) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            color = if (isSystemInDarkTheme()) Black30 else White.copy(alpha = 0.8f),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .widthIn(max = 448.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isSystemInDarkTheme()) White.copy(alpha = 0.1f) else White.copy(
                        alpha = 0.2f
                    ),
                    shape = RoundedCornerShape(32.dp)
                ),
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    val isActive = currentRoute == item.route::class.qualifiedName

                    val contentColor by animateColorAsState(
                        targetValue = if (isActive) {
                            if (isSystemInDarkTheme()) Blue400 else Blue600
                        } else {
                            if (isSystemInDarkTheme()) Gray400 else Gray600
                        }, label = "nav_item_color"
                    )

                    val backgroundColor = if (isActive) {
                        if (isSystemInDarkTheme()) Blue400.copy(alpha = 0.2f) else Blue500.copy(
                            alpha = 0.2f
                        )
                    } else {
                        Color.Transparent
                    }

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(backgroundColor)
                            .clickable { onNavigate(item.route) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = contentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}