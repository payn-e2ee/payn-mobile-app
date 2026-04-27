package com.example.payn.chat.data.service

import android.util.Log
import com.example.payn.chat.data.dto.MessageFrameDTO
import com.example.payn.chat.data.security.DoubleRatchetEngine
import com.example.payn.chat.domain.ChatMessage
import com.example.payn.core.config.AppConfig
import com.example.payn.core.data.AuthSessionManager
import com.example.payn.core.data.network.MqttWebSocketClient
import com.example.payn.core.domain.models.User
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.util.UUID

class ChatService(
    val mqttWebSocketClient: MqttWebSocketClient,
    val doubleRatchetEngine: DoubleRatchetEngine,
    val authSessionManager: AuthSessionManager
) {

    suspend fun initializeChat(onReady: () -> Unit) {
        log("initializeChat")

        val username = authSessionManager.getUser()?.username ?: ""
        val accessToken = authSessionManager.getAccessToken() ?: ""
        mqttWebSocketClient.connect(
            identifier = UUID.randomUUID().toString(),
            username = username,
            password = "Bearer $accessToken",
            serverHost = AppConfig.WS_ADDRESS,
            serverPort = AppConfig.WS_PORT,
            serverPath = "/chat",
            onConnected = onReady
        )
    }

    suspend fun getMessageFramesForInitChat(
        content: String,
        user: User,
        currentUser: User,
    ): List<MessageFrameDTO> {
        log("getMessageFramesForInitChat")
        var messageFrameDTOs: List<MessageFrameDTO> = emptyList()
        user.devices.forEach { device ->
            log("sendMessage to ${device.id}")
            val messageFrame = doubleRatchetEngine.encryptMessage(
                content = content,
                chatId = "",

                userId = currentUser.id,
                deviceId = currentUser.devices.first().id,
                recipientUserId = user.id,
                remoteDeviceId = device.id,

                remoteIdentityKey = device.identityKey,
                localIdentityKey = currentUser.devices.first().identityKey
            )
            messageFrameDTOs = messageFrameDTOs + messageFrame
        }
        return messageFrameDTOs
    }

    suspend fun sendMessage(
        chatId: String,
        content: String,
        user: User,
        currentUser: User,
    ) {
        log("sendMessage")
        var messageFrameDTOs: List<MessageFrameDTO> = emptyList()
        user.devices.forEach { device ->
            log("sendMessage to ${device.id}")
            val messageFrame = doubleRatchetEngine.encryptMessage(
                content = content,
                chatId = chatId,

                userId = currentUser.id,
                deviceId = currentUser.devices.first().id,
                recipientUserId = user.id,
                remoteDeviceId = device.id,

                remoteIdentityKey = device.identityKey,
                localIdentityKey = currentUser.devices.first().identityKey
            )
            messageFrameDTOs = messageFrameDTOs + messageFrame
        }
        val messageFramesJSON = Json.encodeToString<List<MessageFrameDTO>>(messageFrameDTOs)
        mqttWebSocketClient.publish(
            topic = "chat/$chatId",
            payload = messageFramesJSON.toByteArray(),
        )
    }

    fun subscribeToChat(chatId: String, callback: (ChatMessage) -> Unit) {
        log("subscribeToChat")
        mqttWebSocketClient.subscribe("chat/$chatId") { packet ->
            log("message received")
            runBlocking {
                val payload = StandardCharsets.UTF_8.decode(packet.payload.get()).toString()
                val messageFrameDTO = Json.decodeFromString<MessageFrameDTO>(payload)
                log("senderEphemeralPublicKey='${messageFrameDTO.header.senderEphemeralPublicKey}', senderIdentityKey='${messageFrameDTO.header.senderIdentityKey}")
                val plaintext = doubleRatchetEngine.decryptMessage(
                    ciphertext = messageFrameDTO.ciphertext,
                    remoteEphemeralPublicKey = messageFrameDTO.header.senderEphemeralPublicKey,
                    remoteDeviceId = messageFrameDTO.header.senderDeviceId,
                    messageCounter = messageFrameDTO.header.messageCounter,
                )
                callback(
                    ChatMessage(
                        id = messageFrameDTO.header.messageId,
                        content = String(plaintext),
                        userId = messageFrameDTO.header.senderUserId,
                        createdAt = System.currentTimeMillis().toString()
                    )
                )
            }
        }
    }

    fun destroyChat() {
        mqttWebSocketClient.disconnect()
    }

    private fun log(message: String) {
        Log.println(
            Log.INFO,
            "ChatService",
            message
        )
    }
}