package com.example.payn.chat.data

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class AudioRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    var outputFile: File? = null

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    fun start() {
        if (mediaRecorder != null) return

        outputFile = File(context.cacheDir, "voice_${System.currentTimeMillis()}.m4a")

        mediaRecorder = createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile?.absolutePath)

            prepare()
            start()
        }
    }

    fun stop() {
        mediaRecorder?.apply {
            try {
                stop()
            } catch (_: Exception) {
                // Handle case where stop is called before start is successful
            }
            reset()
            release()
        }
        mediaRecorder = null
    }
}