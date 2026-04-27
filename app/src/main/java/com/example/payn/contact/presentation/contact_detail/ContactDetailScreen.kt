package com.example.payn.contact.presentation.contact_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.Trash2
import com.example.payn.R
import com.example.payn.core.presentation.components.GlassCard
import com.example.payn.ui.theme.Blue400
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Blue600
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.Pink400
import com.example.payn.ui.theme.Purple400
import com.example.payn.ui.theme.Red500
import com.example.payn.ui.theme.White

@Composable
fun ContactDetailScreen(
    viewModel: ContactDetailViewModel,
    navController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    listOf(Blue400, Purple400, Pink400)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .widthIn(max = 420.dp)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GlassCard(
                    modifier = Modifier.size(48.dp),
                    onClick = { navController.navigateUp() }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Text(
                    text = "Contact Info",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            state.contact?.let { contact ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            // FIXME: Use user profile picture when it available
                            Image(
                                painter = painterResource(R.drawable.default_profile_picture),
                                contentDescription = "profile picture",
                                modifier = Modifier
                                    .size(128.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, White.copy(alpha = 0.2f), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${contact.firstname} ${contact.lastname}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Gray900
                            )
                            if (contact.contactUser != null) {
                                Text(
                                    text = "@${contact.contactUser.username}",
                                    fontSize = 14.sp,
                                    color = Gray600
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Blue500)
                                    .clickable {
                                        // FIXME: Go to chat screen
                                        // navController.navigate(Route.Chat)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Lucide.MessageCircle,
                                        contentDescription = null,
                                        tint = White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Message",
                                        color = White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Contact Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gray900
                        )

                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Blue500.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Phone,
                                    contentDescription = null,
                                    tint = Blue600,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(text = "Phone", fontSize = 14.sp, color = Gray600)
                                Text(
                                    text = contact.contactUser?.phoneNumber ?: "N/A",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Gray900
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Blue500.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Pencil,
                                    contentDescription = null,
                                    tint = Blue600,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "Edit Contact",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Gray900
                            )
                        }
                    }

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.deleteContact {
                                navController.navigateUp()
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Red500.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Trash2,
                                    contentDescription = null,
                                    tint = Red500,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "Delete Contact",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Red500
                            )
                        }
                    }
                }
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Contact not found", color = White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
