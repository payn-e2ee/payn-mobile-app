package com.example.payn.app


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.payn.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(24.dp)

    val baseModifier = modifier
        .clip(shape)

    val clickableModifier = if (onClick != null) {
        baseModifier.clickable(onClick = onClick)
    } else {
        baseModifier
    }

    Box(modifier = clickableModifier) {

        // 🔹 Background blur layer (ONLY this is blurred)
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(20.dp) // backdrop blur effect
                .background(White.copy(alpha = 0.7f))
        )

        // 🔹 Border layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(
                    width = 1.dp,
                    color = White20,
                    shape = shape
                )
        )

        // 🔹 Content (NOT blurred)
        Box {
            content()
        }
    }
}

@Preview
@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit = {},
    onSignupClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Blue400,
                        Purple400,
                        Pink400
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {

            // Logo + Branding
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                ) {

                    // 🔹 Blurred background layer
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .blur(20.dp)
                            .background(White.copy(alpha = 0.3f))
                    )

                    // 🔹 Border layer
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .border(
                                width = 1.dp,
                                color = White20,
                                shape = CircleShape
                            )
                    )

                    // 🔹 Content (sharp, not blurred)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "\uD83D\uDCAC",
                            fontSize = 48.sp,
                            color = White
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "GlassChat",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Connect with everyone, beautifully",
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLoginClick() }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Login",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gray900
                        )
                    }
                }

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSignupClick() }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Create Account",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gray900
                        )
                    }
                }
            }

            // Features Card
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureItem("💬", "One-to-one messaging")
                    FeatureItem("📞", "Voice calling")
                    FeatureItem("📷", "Share photos & videos")
                    FeatureItem("🔒", "Secure & private")
                }
            }
        }
    }
}

@Composable
fun FeatureItem(icon: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        Text(
            text = text,
            fontSize = 16.sp,
            color = Gray700
        )
    }
}