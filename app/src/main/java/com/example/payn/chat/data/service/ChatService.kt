package com.example.payn.chat.data.service

import android.util.Log
import com.example.payn.chat.data.dto.MessageFrameDTO
import com.example.payn.chat.data.dto.MessageHeaderDTO
import com.example.payn.chat.data.dto.MessageTypeDTO
import com.example.payn.chat.data.mappers.toMessageType
import com.example.payn.chat.data.security.DoubleRatchetEngine
import com.example.payn.chat.domain.ChatMessage
import com.example.payn.core.config.AppConfig
import com.example.payn.core.data.AuthSessionManager
import com.example.payn.core.data.network.MqttWebSocketClient
import com.example.payn.core.data.repository.AttachmentRepository
import com.example.payn.core.domain.models.User
import com.example.payn.core.domain.onSuccess
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.util.UUID
import android.util.Base64
import com.example.payn.chat.data.dto.InitChatDTO
import com.example.payn.chat.data.mappers.toChat
import com.example.payn.chat.data.repository.ChatRepository
import com.example.payn.core.data.dto.AttachmentDTO
import com.example.payn.core.data.dto.UploadAttachmentFormDTO
import com.example.payn.core.data.mappers.toAttachment
import com.example.payn.core.domain.models.Attachment

class ChatService(
    val mqttWebSocketClient: MqttWebSocketClient,
    val doubleRatchetEngine: DoubleRatchetEngine,
    val authSessionManager: AuthSessionManager,
    val chatRepository: ChatRepository,
    val attachmentRepository: AttachmentRepository,
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

    suspend fun sendMessage(
        chatId: String?,
        content: ByteArray,
        messageType: MessageTypeDTO,
        user: User,
        currentUser: User,
        originalFileName: String? = null,
        originalFileSize: Long? = null
    ): List<MessageFrameDTO> {
        log("sendMessage")
        var messageFrameDTOs: List<MessageFrameDTO> = emptyList()
        for (device in user.devices) {
            log("sendMessage to ${device.id}")
            val encryptedMessage = doubleRatchetEngine.encryptMessage(
                content = content,
                remoteDeviceId = device.id,
                remoteIdentityKey = device.identityKey,
            )

            val ciphertext = if (messageType == MessageTypeDTO.TEXT) Base64.encodeToString(
                encryptedMessage.ciphertext,
                Base64.DEFAULT
            ) else ""
            var attachment: AttachmentDTO? = null
            if (messageType == MessageTypeDTO.IMAGE || messageType == MessageTypeDTO.VOICE || messageType == MessageTypeDTO.FILE || messageType == MessageTypeDTO.VIDEO) {
                attachmentRepository.uploadAttachment(
                    uploadAttachmentFormDTO = UploadAttachmentFormDTO(
                        originalFileName = originalFileName!!,
                        originalFileSize = originalFileSize!!
                    ),
                    fileBytes = encryptedMessage.ciphertext
                )
                    .onSuccess { response ->
                        attachment = response.data
                    }
            }

            messageFrameDTOs = messageFrameDTOs + MessageFrameDTO(
                header = MessageHeaderDTO(
                    chatId = chatId ?: "",
                    senderUserId = currentUser.id,
                    senderDeviceId = currentUser.devices.first().id,
                    recipientUserId = user.id,
                    recipientDeviceId = device.id,
                    senderIdentityKey = currentUser.devices.first().identityKey,
                    senderEphemeralPublicKey = encryptedMessage.ephemeralPublicKey,
                    messageCounter = encryptedMessage.messageCounter,
                    messageType = messageType,
                    attachment = attachment,
                    messageId = "",
                ),
                ciphertext = ciphertext,
                authTag = "",
            )
        }
        val messageFramesJSON = Json.encodeToString<List<MessageFrameDTO>>(messageFrameDTOs)
        if (chatId == null) {
            chatRepository.initChat(InitChatDTO(messageFrameDTOs)).onSuccess { response ->
                messageFrameDTOs.forEach {
                    it.header.chatId = response.data.toChat().id
                }
            }
        } else {
            mqttWebSocketClient.publish(
                topic = "chat/$chatId",
                payload = messageFramesJSON.toByteArray(),
            )
        }
        return messageFrameDTOs
    }

    fun subscribeToChat(chatId: String, callback: (ChatMessage) -> Unit) {
        log("subscribeToChat")
        mqttWebSocketClient.subscribe("chat/$chatId") { packet ->
            log("message received")
            runBlocking {
                val payload = StandardCharsets.UTF_8.decode(packet.payload.get()).toString()
                val messageFrameDTO = Json.decodeFromString<MessageFrameDTO>(payload)
                log("senderEphemeralPublicKey='${messageFrameDTO.header.senderEphemeralPublicKey}', senderIdentityKey='${messageFrameDTO.header.senderIdentityKey}")

                callback(
                    ChatMessage(
                        id = messageFrameDTO.header.messageId,
                        ciphertext = messageFrameDTO.ciphertext,
                        userId = messageFrameDTO.header.senderUserId,
                        messageType = messageFrameDTO.header.messageType.toMessageType(),
                        attachment = messageFrameDTO.header.attachment?.toAttachment(),
                        deviceId = messageFrameDTO.header.senderDeviceId,
                        messageCounter = messageFrameDTO.header.messageCounter,
                        ephemeralPublicKey = messageFrameDTO.header.senderEphemeralPublicKey,
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