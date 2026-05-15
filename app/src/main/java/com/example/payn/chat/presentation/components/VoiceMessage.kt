package com.example.payn.chat.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.LoaderCircle
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pause
import com.composables.icons.lucide.Play
import com.example.payn.chat.data.AudioPlayer
import com.example.payn.chat.domain.MessageStatus
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.White
import kotlin.math.abs

@Composable
fun VoiceMessage(
    content: ByteArray,
    status: MessageStatus,
    isMe: Boolean,
    createdAt: String,
    isLoading: Boolean,
) {
    val accentColor = if (isMe) White else Gray900
    val playedColor = if (isMe) White else Blue500
    val unplayedColor = if (isMe) White.copy(alpha = 0.4f) else Gray600.copy(alpha = 0.3f)

    if (isLoading) {
        val infiniteTransition = rememberInfiniteTransition(label = "loading")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = "rotation"
        )

        Box(
            modifier = Modifier
                .widthIn(min = 150.dp, max = 280.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(if (isMe) Blue500 else White.copy(alpha = 0.7f))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(32.dp)
            ) {
                Icon(
                    imageVector = Lucide.LoaderCircle,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.rotate(rotation)
                )

                Spacer(modifier = Modifier.width(8.dp))

                LoadingWaveformPlaceholder(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = unplayedColor
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = formatIsoDate(createdAt),
                    fontSize = 10.sp,
                    color = if (isMe) White.copy(alpha = 0.7f) else Gray600
                )
            }
        }
        return
    }

    var isPlaying by remember { mutableStateOf(false) }
    var progress: Float by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    val audioPlayer = remember(content) {
        AudioPlayer(
            scope = scope,
            audioBytes = content,
            onProgress = { progress = it },
            onCompletion = { isPlaying = false },
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.dispose()
        }
    }

    Box(
        modifier = Modifier
            .widthIn(max = 280.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (isMe) Blue500 else White.copy(alpha = 0.7f))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(32.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Lucide.Pause else Lucide.Play,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.clickable {
                    if (isPlaying) audioPlayer.pause() else audioPlayer.play()
                    isPlaying = !isPlaying
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            WaveformDisplay(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                amplitudes = content,
                progress = progress,
                playedColor = playedColor,
                unplayedColor = unplayedColor,
                onSeek = {
                    audioPlayer.seekTo(it)
                    progress = it
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = formatIsoDate(createdAt),
                fontSize = 10.sp,
                color = if (isMe) White.copy(alpha = 0.7f) else Gray600
            )

            if (isMe) {
                Spacer(modifier = Modifier.width(4.dp))
                MessageStatus(status)
            }
        }
    }
}

@Composable
fun WaveformDisplay(
    modifier: Modifier,
    amplitudes: ByteArray,
    progress: Float,
    playedColor: Color,
    unplayedColor: Color,
    onSeek: (Float) -> Unit
) {
    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                val percentage = (offset.x / size.width).coerceIn(0f, 1f)
                onSeek(percentage)
            }
        }
    ) {
        val barWidth = 3.dp.toPx()
        val gapWidth = 2.dp.toPx()
        val totalBarWidth = barWidth + gapWidth
        val maxBarCount = (size.width / totalBarWidth).toInt()

        val step =
            if (amplitudes.isNotEmpty()) (amplitudes.size / maxBarCount).coerceAtLeast(1) else 1

        for (i in 0 until maxBarCount) {
            val dataIndex = i * step
            if (dataIndex >= amplitudes.size) break

            val amplitude = abs(amplitudes[dataIndex].toInt()).toFloat()
            val normalizedHeight = (amplitude / 128f) * size.height
            val finalHeight = normalizedHeight.coerceAtLeast(4.dp.toPx())

            // Determine if this bar is in the "played" zone
            val barPosition = i.toFloat() / maxBarCount
            val isPlayed = barPosition <= progress

            drawRoundRect(
                color = if (isPlayed) playedColor else unplayedColor,
                topLeft = Offset(
                    x = i * totalBarWidth,
                    y = (size.height - finalHeight) / 2
                ),
                size = Size(barWidth, finalHeight),
                cornerRadius = CornerRadius(barWidth / 2)
            )
        }
    }
}

@Composable
fun LoadingWaveformPlaceholder(
    modifier: Modifier,
    color: Color
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    Canvas(modifier = modifier) {
        val barWidth = 3.dp.toPx()
        val gapWidth = 2.dp.toPx()
        val totalBarWidth = barWidth + gapWidth
        val maxBarCount = (size.width / totalBarWidth).toInt()

        for (i in 0 until maxBarCount) {
            // Static heights for the placeholder to mimic a waveform
            val mockHeight = (size.height * (0.3f + (i % 3) * 0.2f))

            drawRoundRect(
                color = color.copy(alpha = alpha),
                topLeft = Offset(
                    x = i * totalBarWidth,
                    y = (size.height - mockHeight) / 2
                ),
                size = Size(barWidth, mockHeight),
                cornerRadius = CornerRadius(barWidth / 2)
            )
        }
    }
}