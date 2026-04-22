package com.example.payn.auth.presentation.register

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.example.payn.app.Route
import com.example.payn.auth.presentation.components.GlassCard
import com.example.payn.auth.presentation.components.Input
import com.example.payn.auth.presentation.components.InputType
import com.example.payn.auth.presentation.components.OtpInput
import com.example.payn.ui.theme.Blue400
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Pink400
import com.example.payn.ui.theme.Purple400

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
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

            // Back Button
            GlassCard(
                modifier = Modifier.size(48.dp),
                onClick = {
                    if (state.step > 1) {
                        viewModel.previousStep()
                    } else {
                        navController.navigateUp()
                    }
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
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .blur(20.dp)
                            .background(White.copy(alpha = 0.3f))
                    )
                    Text(
                        text = "\uD83D\uDCAC", fontSize = 44.sp, color = White
                    )
                }

                Text(
                    text = "Create Account",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )

                Text(
                    text = when (state.step) {
                        1 -> "Enter your phone number"
                        2 -> "Enter the verification code"
                        else -> "Set up your profile"
                    },
                    fontSize = 14.sp,
                    color = White.copy(alpha = 0.8f)
                )
            }

            // Progress Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                listOf(1, 2, 3).forEach { step ->
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(64.dp)
                            .clip(CircleShape)
                            .background(
                                if (step <= state.step) White else White.copy(alpha = 0.3f)
                            )
                    )
                }
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
                    when (state.step) {
                        1 -> {
                            Input(
                                label = "Phone Number",
                                value = state.phone,
                                onValueChange = { viewModel.onPhoneChange(it) },
                                placeholder = "Enter your phone number",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                errorMessage = state.phoneFieldError
                            )

                            SubmitButton(text = "Send Code", isLoading = state.isLoading) {
                                viewModel.sendOtp(onError = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                })
                            }
                        }

                        2 -> {
                            OtpInput(
                                value = state.otp,
                                onValueChange = { viewModel.onOtpChange(it) },
                                errorMessage = state.otpFieldError
                            )

                            SubmitButton(text = "Verify", isLoading = state.isLoading) {
                                viewModel.verifyOtp(onError = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                })
                            }

                            Text(
                                text = "Resend Code",
                                color = White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { /* Handle resend */ }
                            )
                        }

                        3 -> {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    Input(
                                        label = "First Name",
                                        value = state.firstName,
                                        onValueChange = { viewModel.onFirstNameChange(it) },
                                        placeholder = "John"
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    Input(
                                        label = "Last Name",
                                        value = state.lastName,
                                        onValueChange = { viewModel.onLastNameChange(it) },
                                        placeholder = "Doe"
                                    )
                                }
                            }

                            Input(
                                label = "Username",
                                value = state.username,
                                onValueChange = { viewModel.onUsernameChange(it) },
                                placeholder = "Enter your Username",
                                errorMessage = state.usernameFieldError
                            )

                            Input(
                                label = "Password",
                                value = state.password,
                                onValueChange = { viewModel.onPasswordChange(it) },
                                placeholder = "Choose a password",
                                type = InputType.Password,
                                errorMessage = state.passwordFieldError
                            )

                            SubmitButton(text = "Complete Setup", isLoading = state.isLoading) {
                                viewModel.submit(
                                    onSuccess = {
                                        navController.navigate(Route.Login) {
                                            popUpTo(Route.Welcome) { inclusive = true }
                                        }
                                    },
                                    onError = {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (state.step == 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account? ",
                        fontSize = 14.sp,
                        color = White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Login",
                        fontSize = 14.sp,
                        color = White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            navController.navigate(Route.Login)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SubmitButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Blue500)
            .clickable(enabled = !isLoading) { onClick() }
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isLoading) "Loading..." else text,
            color = White,
            fontWeight = FontWeight.SemiBold
        )
    }
}