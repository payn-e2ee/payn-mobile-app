package com.example.payn.settings.presentation.edit_profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.AtSign
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.User
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import com.example.payn.app.Route
import com.example.payn.core.config.AppConfig
import com.example.payn.core.presentation.components.GlassCard
import com.example.payn.settings.presentation.components.Input
import com.example.payn.ui.theme.Blue400
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Blue600
import com.example.payn.ui.theme.Blue900
import com.example.payn.ui.theme.Gray200
import com.example.payn.ui.theme.Gray300
import com.example.payn.ui.theme.Gray400
import com.example.payn.ui.theme.Gray500
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Gray700
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.Pink400
import com.example.payn.ui.theme.Pink900
import com.example.payn.ui.theme.Purple400
import com.example.payn.ui.theme.Purple900
import com.example.payn.ui.theme.White
import com.example.payn.ui.theme.White10
import com.example.payn.ui.theme.White20
import com.example.payn.ui.theme.White30

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    navController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val gradientBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(Blue900, Purple900, Pink900)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Blue400, Purple400, Pink400)
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.onImageSelected(it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(White10)
                ) {
                    Icon(
                        imageVector = Lucide.ArrowLeft,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Edit Profile",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Update your personal information",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Profile Picture
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(128.dp)
                    ) {
                        // Profile Image/Circle
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(White10)
                                .border(
                                    width = 1.dp,
                                    color = White20,
                                    shape = CircleShape
                                )
                                .clickable { launcher.launch("image/*") }
                        ) {
                            val imageSource = if (state.profileImageBytes != null) {
                                state.profileImageBytes
                            } else if (state.profileImageId != null) {
                                "${AppConfig.BASE_API_URL}/attachments/${state.profileImageId}"
                            } else {
                                null
                            }

                            if (imageSource != null) {
                                AsyncImage(
                                    model = imageSource,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .border(4.dp, White20, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(Blue500, Purple900)
                                            )
                                        )
                                        .border(4.dp, White20, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.firstname.take(1).ifEmpty { "U" },
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Camera Icon (Overlay - outside the clipped box)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 4.dp, y = 4.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Blue500)
                                .border(2.dp, if (isDark) Gray900 else White, CircleShape)
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Camera,
                                contentDescription = "Change Image",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tap the image or camera icon to change your profile picture",
                        fontSize = 14.sp,
                        color = if (isDark) Gray300 else Gray600,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Personal Information
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Personal Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Gray900,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Input(
                            label = "First Name",
                            value = state.firstname,
                            onValueChange = { viewModel.onFirstNameChange(it) },
                            placeholder = "First Name",
                            leadingIcon = Lucide.User,
                            errorMessage = state.firstnameFieldError,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Input(
                            label = "Last Name",
                            value = state.lastname,
                            onValueChange = { viewModel.onLastNameChange(it) },
                            placeholder = "Last Name",
                            errorMessage = state.lastnameFieldError,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Input(
                        label = "Username",
                        value = state.username,
                        onValueChange = { viewModel.onUsernameChange(it) },
                        placeholder = "Username",
                        errorMessage = state.usernameFieldError,
                        leadingIcon = Lucide.AtSign
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contact Information (Read-only)
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Contact Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Gray900,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Phone Number",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Gray300 else Gray700,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isDark) Gray700.copy(alpha = 0.5f) else Gray200.copy(
                                    alpha = 0.5f
                                )
                            )
                            .border(1.dp, White20, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = state.phoneNumber,
                            color = if (isDark) Gray400 else Gray500
                        )
                    }

                    Text(
                        text = "Phone number cannot be changed",
                        fontSize = 12.sp,
                        color = if (isDark) Gray400 else Gray600,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) White10 else White30,
                        contentColor = if (isDark) Color.White else Gray900
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = {
                        viewModel.onSave(
                            onSuccess = {
                                navController.navigate(Route.Settings)
                            },
                            onError = { message ->
                                Toast.makeText(
                                    context, message, Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue500,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save Changes", fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Blue500.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.User,
                            contentDescription = null,
                            tint = if (isDark) Blue400 else Blue600,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Your profile information is visible to your contacts. Make sure to keep it up to date.",
                        fontSize = 14.sp,
                        color = if (isDark) Gray300 else Gray700
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

