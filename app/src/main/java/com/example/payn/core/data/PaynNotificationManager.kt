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

class PaynNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_MESSAGES_ID = "messages_channel"
        const val CHANNEL_MESSAGES_NAME = "Messages"
        const val CHANNEL_MESSAGES_DESCRIPTION = "Notifications for new messages"
        
        const val NOTIFICATION_ID_MESSAGE = 1001
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_MESSAGES_ID, CHANNEL_MESSAGES_NAME, importance).apply {
                description = CHANNEL_MESSAGES_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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

    /**
     * 
     * @param title
     * @param message 
     * @param intent Optional intent to trigger when the notification is clicked
     */
    fun showSimpleNotification(
        title: String,
        message: String,
        intent: Intent? = null
    ) {
        val pendingIntent = intent?.let {
            PendingIntent.getActivity(
                context, 
                0, 
                it, 
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_MESSAGES_ID)
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
    fun showExpandableNotification(
        title: String,
        shortMessage: String,
        longMessage: String
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_MESSAGES_ID)
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
