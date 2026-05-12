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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.material3.Player
import com.composables.icons.lucide.LoaderCircle
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pause
import com.composables.icons.lucide.Play
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.White

@Composable
fun VideoMessage(
    videoUri: Uri?,
    isMe: Boolean,
    createdAt: String,
    isLoading: Boolean,
    showVideoOnFullScreen: (Uri) -> Unit,
) {
    if (isLoading) {
        VideoLoadingPlaceholder(isMe)
        return
    }

    val videoUri = videoUri ?: return
    val context = LocalContext.current
    var videoAspectRatio by remember { mutableStateOf(16f / 9f) } // Default ratio

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = false
        }
    }
    var isPlaying by remember { mutableStateOf(player.isPlaying) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.width > 0 && videoSize.height > 0) {
                    videoAspectRatio = videoSize.width.toFloat() / videoSize.height.toFloat()
                }
            }
        }
        player.addListener(listener)
        onDispose { player.release() }
    }

    Box(
        modifier = Modifier
            .widthIn(max = 280.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (isMe) Blue500 else White.copy(alpha = 0.7f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(videoAspectRatio.coerceIn(0.5f, 2f))
                .clickable {
                    showVideoOnFullScreen(videoUri)
                }
        ) {
            Player(
                player = player,
                modifier = Modifier.fillMaxSize(),
                topControls = null,
                centerControls = { _, isVisible ->
                    if (isVisible) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = { if (isPlaying) player.pause() else player.play() }
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Lucide.Pause else Lucide.Play,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }
                },
                bottomControls = null
            )
        }

        // Timestamp overlay
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .background(Gray900.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(text = formatIsoDate(createdAt), fontSize = 10.sp, color = White)
            if (isMe) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "✓✓", fontSize = 10.sp, color = White)
            }
        }
    }
}

@Composable
private fun VideoLoadingPlaceholder(isMe: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "video_loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)), label = "rot"
    )
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing)),
        label = "shim"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Gray600.copy(alpha = 0.1f),
            Gray600.copy(alpha = 0.3f),
            Gray600.copy(alpha = 0.1f)
        ),
        start = Offset.Zero,
        end = Offset(shimmerTranslate, shimmerTranslate)
    )

    Box(
        modifier = Modifier
            .width(240.dp)
            .aspectRatio(4f / 3f)
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
}