package com.example.payn.chat.presentation.components

import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.composables.icons.lucide.LoaderCircle
import com.composables.icons.lucide.Lucide
import com.example.payn.core.data.mappers.toUri
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.White

@Composable
fun ImageMessage(
    content: ByteArray,
    isMe: Boolean,
    createdAt: String,
    isLoading: Boolean,
    showImageOnFullScreen: (Uri) -> Unit,
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .widthIn(max = 280.dp)
            .clip(RoundedCornerShape(24.dp))
            .then(
                if (isMe) {
                    Modifier.background(Blue500)
                } else {
                    Modifier
                        .background(White.copy(alpha = 0.7f))
                }
            )
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            if (isLoading) {
                // Shimmer & Rotation Animation
                val infiniteTransition = rememberInfiniteTransition(label = "loading")

                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing)
                    ), label = "rotation"
                )

                val shimmerTranslate by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1000f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing)
                    ), label = "shimmer"
                )

                val shimmerColors = listOf(
                    Gray600.copy(alpha = 0.1f),
                    Gray600.copy(alpha = 0.3f),
                    Gray600.copy(alpha = 0.1f),
                )

                val brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset.Zero,
                    end = Offset(x = shimmerTranslate, y = shimmerTranslate)
                )

                Box(
                    modifier = Modifier
                        .width(240.dp)
                        .aspectRatio(4f / 3f) // Consistent placeholder ratio
                        .background(brush),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.LoaderCircle,
                        contentDescription = null,
                        tint = if (isMe) White.copy(alpha = 0.5f) else Gray600.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(32.dp)
                            .rotate(rotation)
                    )
                }
            } else {
                val imageUri = content.toUri(context)
                AsyncImage(
                    model = imageUri,
                    contentDescription = "image",
                    modifier = Modifier.clickable {
                        showImageOnFullScreen(imageUri)
                    }
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        color = Gray900.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = formatIsoDate(createdAt),
                    fontSize = 10.sp,
                    color = White
                )

                if (isMe) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "✓✓",
                        fontSize = 10.sp,
                        color = White
                    )
                }
            }
        }
    }
}