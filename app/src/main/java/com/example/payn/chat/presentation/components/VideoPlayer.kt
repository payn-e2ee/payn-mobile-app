package com.example.payn.chat.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.material3.Player
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pause
import com.composables.icons.lucide.Play
import com.composables.icons.lucide.RotateCcw
import com.composables.icons.lucide.RotateCw
import com.composables.icons.lucide.SkipBack
import com.composables.icons.lucide.SkipForward
import com.composables.icons.lucide.StepBack
import com.composables.icons.lucide.StepForward
import com.composables.icons.lucide.Volume2
import com.composables.icons.lucide.VolumeX
import kotlinx.coroutines.delay

@Composable
fun VideoPlayer(videoUri: Uri, modifier: Modifier) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = true
        }
    }

    // State for UI synchronization
    var isPlaying by remember { mutableStateOf(player.isPlaying) }
    var isMuted by remember { mutableStateOf(player.volume == 0f) }
    var position by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }

    // Sync playing and duration state
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(state: Int) {
                duration = player.duration.coerceAtLeast(0L)
            }
        }
        player.addListener(listener)
        onDispose { player.release() }
    }

    // Polling for progress position
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                position = player.currentPosition
                delay(500)
            }
        }
    }

    Player(
        player = player,
        modifier = modifier,
        topControls = null,
        centerControls = { _, isVisible ->
            if (isVisible) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    IconButton(onClick = { player.seekBack() }) {
                        Icon(Lucide.RotateCcw, null, tint = White)
                    }

                    IconButton(
                        onClick = { if (isPlaying) player.pause() else player.play() }
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Lucide.Pause else Lucide.Play,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    IconButton(onClick = { player.seekForward() }) {
                        Icon(Lucide.RotateCw, null, tint = White)
                    }
                }
            }
        },
        bottomControls = { _, isVisible ->
            if (isVisible) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Timer start
                    Text(
                        text = formatTime(position),
                        color = White,
                        style = MaterialTheme.typography.labelMedium
                    )

                    // White Progress Bar
                    Slider(
                        value = position.toFloat(),
                        onValueChange = {
                            position = it.toLong()
                            player.seekTo(it.toLong())
                        },
                        valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = White,
                            activeTrackColor = White,
                            inactiveTrackColor = White.copy(alpha = 0.3f)
                        )
                    )

                    // Mute Button at end
                    IconButton(onClick = {
                        isMuted = !isMuted
                        player.volume = if (isMuted) 0f else 1f
                    }) {
                        Icon(
                            imageVector = if (isMuted) Lucide.VolumeX else Lucide.Volume2,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    )
}

// Helper to format ms to 00:00
private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}