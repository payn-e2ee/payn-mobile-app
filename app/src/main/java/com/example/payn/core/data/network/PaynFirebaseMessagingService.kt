package com.example.payn.core.data.network

import android.util.Log
import com.example.payn.core.data.AuthSessionManager
import com.example.payn.core.data.PaynNotificationManager
import com.example.payn.core.data.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PaynFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationManager: PaynNotificationManager by inject()
    private val userRepository: UserRepository by inject()
    private val authSessionManager: AuthSessionManager by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("PaynFCM", "New token generated: $token")
        
        // we only update the token on the server if the user is logged in
        serviceScope.launch {
            if (authSessionManager.isLoggedIn()) {
                userRepository.updateFcmToken(token)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("PaynFCM", "Message received from: ${message.from}")

        // handle data payload
        if (message.data.isNotEmpty()) {
            val title = message.data["title"] ?: "New Message"
            val body = message.data["body"] ?: ""
            
            notificationManager.showSimpleNotification(
                title = title,
                message = body
            )
        }

        // handle notification payload (if any)
        message.notification?.let {
            notificationManager.showSimpleNotification(
                title = it.title ?: "New Notification",
                message = it.body ?: ""
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // no need to cancel scope here as it's tied to the process if we use SupervisorJob
    }
}
