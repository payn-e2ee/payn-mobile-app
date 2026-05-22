package com.example.payn.core.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.payn.R
import kotlinx.coroutines.flow.first

class PaynNotificationManager(
    private val context: Context,
    private val keyValueStorage: KeyValueStorage
) {

    companion object {
        const val CHANNEL_SOUND_VIBE = "messages_channel_sound_vibe"
        const val CHANNEL_SOUND_NOVIBE = "messages_channel_sound_novibe"
        const val CHANNEL_NOSOUND_VIBE = "messages_channel_nosound_vibe"
        const val CHANNEL_NOSOUND_NOVIBE = "messages_channel_nosound_novibe"
        
        const val NOTIFICATION_ID_MESSAGE = 1001
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Sound: Yes, Vibration: Yes
            val chSoundVibe = NotificationChannel(
                CHANNEL_SOUND_VIBE,
                "Messages (Sound & Vibration)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications with sound and vibration"
                enableLights(true)
                enableVibration(true)
            }

            // 2. Sound: Yes, Vibration: No
            val chSoundNoVibe = NotificationChannel(
                CHANNEL_SOUND_NOVIBE,
                "Messages (Sound Only)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications with sound only"
                enableLights(true)
                enableVibration(false)
                vibrationPattern = longArrayOf(0)
            }

            // 3. Sound: No, Vibration: Yes
            val chNoSoundVibe = NotificationChannel(
                CHANNEL_NOSOUND_VIBE,
                "Messages (Vibration Only)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications with vibration only"
                enableLights(true)
                enableVibration(true)
                setSound(null, null)
            }

            // 4. Sound: No, Vibration: No
            val chNoSoundNoVibe = NotificationChannel(
                CHANNEL_NOSOUND_NOVIBE,
                "Messages (Silent)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Silent notifications"
                enableLights(true)
                enableVibration(false)
                vibrationPattern = longArrayOf(0)
                setSound(null, null)
            }

            notificationManager.createNotificationChannel(chSoundVibe)
            notificationManager.createNotificationChannel(chSoundNoVibe)
            notificationManager.createNotificationChannel(chNoSoundVibe)
            notificationManager.createNotificationChannel(chNoSoundNoVibe)
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // permissions are granted by default on older versions
        }
    }

    private suspend fun getSelectedChannelId(): String {
        val sound = keyValueStorage.getBoolean("notification_sound_enabled", true).first()
        val vibe = keyValueStorage.getBoolean("vibration_enabled", true).first()
        
        return when {
            sound && vibe -> CHANNEL_SOUND_VIBE
            sound && !vibe -> CHANNEL_SOUND_NOVIBE
            !sound && vibe -> CHANNEL_NOSOUND_VIBE
            else -> CHANNEL_NOSOUND_NOVIBE
        }
    }

    /**
     * Shows a simple notification.
     * 
     * @param title
     * @param message 
     * @param intent Optional intent to trigger when the notification is clicked
     */
    suspend fun showSimpleNotification(
        title: String,
        message: String,
        intent: Intent? = null
    ) {
        val enabled = keyValueStorage.getBoolean("message_notifications_enabled", true).first()
        if (!enabled) {
            return
        }

        val channelId = getSelectedChannelId()

        val pendingIntent = intent?.let {
            PendingIntent.getActivity(
                context, 
                0, 
                it, 
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(context)) {
            if (hasNotificationPermission()) {
                notify(NOTIFICATION_ID_MESSAGE, builder.build())
            }
        }
    }

    /**
     * Shows an expandable notification with long text content.
     * 
     * @param title
     * @param shortMessage
     * @param longMessage the message shown when expanded
     */
    suspend fun showExpandableNotification(
        title: String,
        shortMessage: String,
        longMessage: String
    ) {
        val enabled = keyValueStorage.getBoolean("message_notifications_enabled", true).first()
        if (!enabled) {
            return
        }

        val channelId = getSelectedChannelId()

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(shortMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(longMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (hasNotificationPermission()) {
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        }
    }

    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}
