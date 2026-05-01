package com.example.payn.chat.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.Video
import com.example.payn.core.domain.models.User
import com.example.payn.core.presentation.components.GlassCard
import com.example.payn.ui.theme.Gray900


@Preview
@Composable
fun ChatDetailHeaderPreview() {
    ChatDetailHeader(
        rememberNavController(),
        User(
            id = "",
            firstname = "Abdelfetah",
            lastname = "Dev",
            username = "abdelfetah",
            phoneNumber = ""
        )
    )
}

@Composable
fun ChatDetailHeader(
    navController: NavHostController,
    user: User
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
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Back
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.ArrowLeft,
                        contentDescription = "arrow-left",
                        Modifier.size(18.dp),
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // FIXME: Use user profile picture when it available
                Image(
                    painter = painterResource(com.example.payn.R.drawable.default_profile_picture),
                    contentDescription = "profile image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${user.firstname} ${user.lastname}",
                        fontWeight = FontWeight.SemiBold,
                        color = Gray900
                    )
//                            Text(
//                                if (true) "Online" else "Offline",
//                                fontSize = 12.sp,
//                                color = Gray600
//                            )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Lucide.Video,
                        contentDescription = "video",
                        Modifier.size(18.dp),
                    )
                    Icon(
                        imageVector = Lucide.Phone,
                        contentDescription = "phone",
                        Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}
