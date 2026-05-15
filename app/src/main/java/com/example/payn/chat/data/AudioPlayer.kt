package com.example.payn.chat.data

import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class AudioPlayer(
    val scope: CoroutineScope,
    val audioBytes: ByteArray,
    val onProgress: (Float) -> Unit,
    val onCompletion: () -> Unit,
) {
    var mediaPlayer: MediaPlayer
    var progressJob: Job? = null

    init {
        val tempFile = File.createTempFile("voice_${System.currentTimeMillis()}", ".mp3")
        tempFile.writeBytes(audioBytes)
        mediaPlayer = MediaPlayer().apply {
            setDataSource(tempFile.absolutePath)
            prepare()
            setOnCompletionListener {
                progressJob?.cancel()
                tempFile.delete()
                onCompletion()
            }
        }
    }

    fun play() {
        mediaPlayer.start()

        progressJob = scope.launch {
            while (isActive && mediaPlayer.isPlaying) {
                val current = mediaPlayer.currentPosition.toFloat()
                val total = mediaPlayer.duration.toFloat()
                onProgress(current / total)
                delay(50)
            }
        }
        progressJob?.start()
    }


    fun pause() {
        mediaPlayer.pause()
        progressJob?.cancel()
    }

    fun seekTo(percentage: Float) {
        mediaPlayer.let {
            val seekMs = (percentage * it.duration).toInt()
            it.seekTo(seekMs)
        }
    }

    fun dispose() {
        mediaPlayer.stop()
        mediaPlayer.release()
        progressJob?.cancel()
    }
}