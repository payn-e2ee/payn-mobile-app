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
import com.example.payn.chat.data.security.DoubleRatchetEngine
import android.util.Base64
import org.koin.android.ext.android.inject

class PaynFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationManager: PaynNotificationManager by inject()
    private val userRepository: UserRepository by inject()
    private val authSessionManager: AuthSessionManager by inject()
    private val doubleRatchetEngine: DoubleRatchetEngine by inject()

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
            serviceScope.launch {
                val type = message.data["type"]
                val title = message.data["title"] ?: "New Message"
                val body = message.data["body"] ?: ""

                if (type == "message") {
                    val ciphertext = message.data["ciphertext"]
                    val ephemeralPublicKey = message.data["ephemeral_public_key"]
                    val messageCounter = message.data["message_counter"]?.toIntOrNull() ?: 0
                    val senderUserId = message.data["sender_user_id"] ?: ""
                    val senderDeviceId = message.data["sender_device_id"] ?: ""

                    if (ciphertext != null && ephemeralPublicKey != null) {
                        try {
                            val decryptedBody = if (doubleRatchetEngine.isFirstTimeSeeingEphemeralPublicKey(ephemeralPublicKey)) {
                                String(
                                    doubleRatchetEngine.decryptMessage(
                                        ciphertext = Base64.decode(ciphertext, Base64.DEFAULT),
                                        remoteEphemeralPublicKey = ephemeralPublicKey,
                                        messageCounter = messageCounter,
                                        remoteDeviceId = senderDeviceId,
                                    )
                                )
                            } else {
                                String(
                                    doubleRatchetEngine.decryptStateless(
                                        ciphertext = Base64.decode(ciphertext, Base64.DEFAULT),
                                        ephemeralPublicKey = ephemeralPublicKey,
                                        messageCounter = messageCounter,
                                        remoteDeviceId = senderDeviceId,
                                        isFromCurrentDevice = false,
                                    )
                                )
                            }

                            notificationManager.showSimpleNotification(
                                title = title,
                                message = decryptedBody
                            )
                        } catch (e: Exception) {
                            Log.e("PaynFCM", "Failed to decrypt message", e)
                            notificationManager.showSimpleNotification(
                                title = title,
                                message = "Sent you a message" // fallback
                            )
                        }
                    } else {
                        notificationManager.showSimpleNotification(
                            title = title,
                            message = "Sent you a message"
                        )
                    }
                } else {
                    notificationManager.showSimpleNotification(
                        title = title,
                        message = body
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // no need to cancel scope here as it's tied to the process if we use SupervisorJob
    }
}
