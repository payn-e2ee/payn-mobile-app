package com.example.payn.auth.presentation.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.example.payn.app.Route
import com.example.payn.auth.presentation.components.GlassCard
import com.example.payn.auth.presentation.components.Input
import com.example.payn.auth.presentation.components.InputType
import com.example.payn.ui.theme.Blue400
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Pink400
import com.example.payn.ui.theme.Purple400


@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    listOf(Blue400, Purple400, Pink400)
                )
            )
            .padding(24.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Back Button (GlassCard icon button)
            GlassCard(
                modifier = Modifier.size(48.dp), onClick = {
                    navController.navigateUp()
                }) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Lucide.ArrowLeft,
                        contentDescription = "arrow-left",
                        Modifier.size(20.dp),
                    )
                }
            }

            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Glass icon circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                ) {

                    // blurred background layer
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .blur(20.dp)
                            .background(White.copy(alpha = 0.3f))
                    )

                    // border layer
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape)
                            .background(Color.Transparent)
                    )

                    // content
                    Text(
                        text = "\uD83D\uDCAC", fontSize = 44.sp, color = White
                    )
                }

                Text(
                    text = "Welcome Back",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )

                Text(
                    text = "Enter your phone number to continue",
                    fontSize = 14.sp,
                    color = White.copy(alpha = 0.8f)
                )
            }

            // Form Glass Card
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Input(
                        label = "Username",
                        value = state.username,
                        onValueChange = { viewModel.onUsernameChange(it) },
                        placeholder = "Type your username",
                        type = InputType.Text,
                        errorMessage = state.usernameFieldError
                    )

                    Input(
                        label = "Password",
                        value = state.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        placeholder = "Type your password",
                        type = InputType.Password,
                        errorMessage = state.passwordFieldError
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Blue500)
                            .clickable {
                                viewModel.submit(
                                    context = context,
                                    onSuccess = { navController.navigate(Route.Chats) },
                                    onError = { message ->
                                        Toast.makeText(
                                            context, message, Toast.LENGTH_LONG
                                        ).show()
                                    })
                            }
                            .padding(14.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Continue", color = White, fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Don't have an account? ", fontSize = 12.sp, color = Gray600
                        )

                        Text(
                            text = "Sign up",
                            fontSize = 12.sp,
                            color = Blue500,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                navController.navigate(Route.Register)
                            })
                    }
                }
            }
        }
    }
}