package com.example.payn.chat.presentation.chat_detail

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.payn.app.Route
import com.example.payn.chat.data.dto.MessageTypeDTO
import com.example.payn.chat.data.mappers.toChat
import com.example.payn.chat.data.mappers.toMessage
import com.example.payn.chat.data.mappers.toMessageType
import com.example.payn.chat.data.repository.ChatRepository
import com.example.payn.chat.data.security.DoubleRatchetEngine
import com.example.payn.chat.data.service.ChatService
import com.example.payn.chat.domain.ChatMessage
import com.example.payn.core.data.AuthSessionManager
import com.example.payn.core.data.FileManager
import com.example.payn.core.data.mappers.toAttachment
import com.example.payn.core.data.mappers.toUser
import com.example.payn.core.data.repository.AttachmentRepository
import com.example.payn.core.data.repository.UserRepository
import com.example.payn.core.domain.onError
import com.example.payn.core.domain.onSuccess
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ChatDetailViewModel(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val attachmentRepository: AttachmentRepository,
    private val chatService: ChatService,
    private val doubleRatchetEngine: DoubleRatchetEngine,
    private val fileManager: FileManager,
    authSessionManager: AuthSessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val chatId = savedStateHandle.toRoute<Route.Chat>().id
    val userId = savedStateHandle.toRoute<Route.Chat>().userId
    val currentUser = authSessionManager.getUser()

    private val _state = MutableStateFlow(ChatDetailState())
    val state = _state
        .onStart {
            if (chatId != null) {
                initChat()
            } else {
                fetchUserById()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private suspend fun initChat() {
        if (chatId != null) {
            fetchChatById().join()
            fetchMessages().join()
            chatService.initializeChat(
                onReady = {
                    chatService.subscribeToChat(
                        chatId = chatId,
                        callback = { message ->
                            appendMessage(message)
                        }
                    )
                }
            )
        }
    }

    private fun fetchChatById(): Job {
        return viewModelScope.launch {
            chatRepository.getChatById(chatId!!)
                .onSuccess { response ->
                    val chat = response.data.toChat()
                    _state.update {
                        it.copy(
                            chat = chat,
                            user = chat.chatMembers?.first { chatMember ->
                                chatMember.user!!.id != currentUser?.id
                            }?.user
                        )
                    }
                }
        }
    }

    fun fetchMessages(): Job {
        return viewModelScope.launch {
            chatRepository.listMessages(chatId!!, _state.value.messages.size)
                .onSuccess { response ->
                    val messages = response.data.map { it.toMessage() }
                    _state.update {
                        it.copy(
                            messages = it.messages + messages.map { message ->
                                val messageDelivery = message.messageDeliveries.first()
                                ChatMessage(
                                    id = message.id,
                                    ciphertext = messageDelivery.ciphertext,
                                    userId = messageDelivery.senderUserId,
                                    messageType = messageDelivery.type,
                                    messageCounter = messageDelivery.messageCounter,
                                    attachment = messageDelivery.attachment,
                                    deviceId = messageDelivery.senderDeviceId,
                                    ephemeralPublicKey = messageDelivery.ephemeralPublicKey,
                                    createdAt = message.createdAt
                                )
                            }
                        )
                    }
                }
        }
    }

    private fun fetchUserById(): Job {
        return viewModelScope.launch {
            userRepository
                .getUserById(userId!!)
                .onSuccess { response ->
                    val user = response.data.toUser()
                    _state.update {
                        it.copy(
                            user = user
                        )
                    }
                }
        }
    }

    fun appendMessage(message: ChatMessage) {
        _state.update {
            it.copy(
                messages = listOf(message) + it.messages
            )
        }
    }

    suspend fun sendImage(onCreateChat: (chatId: String) -> Unit) {
        val selectedImageUri = _state.value.selectedImageUri ?: return
        val content = fileManager.readBytesFromUri(selectedImageUri) ?: return
        sendMessage(
            content = content,
            messageTypeDTO = MessageTypeDTO.IMAGE,
            onCreateChat = onCreateChat,
            originalFileName = fileManager.getFileName(selectedImageUri),
            originalFileSize = fileManager.getFileSize(selectedImageUri),
        )
    }

    suspend fun sendVideo(onCreateChat: (chatId: String) -> Unit) {
        val selectedVideoUri = _state.value.selectedVideoUri ?: return
        val content = fileManager.readBytesFromUri(selectedVideoUri) ?: return
        sendMessage(
            content = content,
            messageTypeDTO = MessageTypeDTO.VIDEO,
            onCreateChat = onCreateChat,
            originalFileName = fileManager.getFileName(selectedVideoUri),
            originalFileSize = fileManager.getFileSize(selectedVideoUri),
        )
    }

    suspend fun sendFile(fileUri: Uri, onCreateChat: (chatId: String) -> Unit) {
        val content = fileManager.readBytesFromUri(fileUri) ?: return
        sendMessage(
            content = content,
            messageTypeDTO = MessageTypeDTO.FILE,
            onCreateChat = onCreateChat,
            originalFileName = fileManager.getFileName(fileUri),
            originalFileSize = fileManager.getFileSize(fileUri)
        )
    }


    suspend fun sendVoice(fileUri: Uri, onCreateChat: (String) -> Unit) {
        val content = fileManager.readBytesFromUri(fileUri) ?: return
        sendMessage(
            content = content,
            messageTypeDTO = MessageTypeDTO.VOICE,
            onCreateChat = onCreateChat,
            originalFileName = fileManager.getFileName(fileUri),
            originalFileSize = fileManager.getFileSize(fileUri)
        )
    }

    suspend fun sendText(content: ByteArray, onCreateChat: (chatId: String) -> Unit) {
        sendMessage(content, MessageTypeDTO.TEXT, onCreateChat)
    }

    suspend fun sendMessage(
        content: ByteArray,
        messageTypeDTO: MessageTypeDTO,
        onCreateChat: (chatId: String) -> Unit,
        originalFileName: String? = null,
        originalFileSize: Long? = null,
    ) {
        val user = _state.value.user ?: return
        val currentUser = currentUser ?: return
        val messageFrames = chatService.sendMessage(
            chatId = chatId,
            content = content,
            messageType = messageTypeDTO,
            user = user,
            currentUser = currentUser,
            originalFileName = originalFileName,
            originalFileSize = originalFileSize
        )
        val messageFrame = messageFrames.first()
        appendMessage(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                ciphertext = messageFrame.ciphertext,
                userId = messageFrame.header.senderUserId,
                attachment = messageFrame.header.attachment?.toAttachment(),
                deviceId = messageFrame.header.senderDeviceId,
                messageCounter = messageFrame.header.messageCounter,
                ephemeralPublicKey = messageFrame.header.senderEphemeralPublicKey,
                messageType = messageFrame.header.messageType.toMessageType(),
                createdAt = System.currentTimeMillis().toString()
            )
        )
        setMessage("")
        if (chatId == null) {
            onCreateChat(messageFrame.header.chatId)
        }
    }

    fun setMessage(content: String) {
        _state.update { it.copy(message = content) }
    }

    suspend fun decryptMessage(
        ciphertext: ByteArray,
        ephemeralPublicKey: String,
        messageCounter: Int,
        userId: String,
        deviceId: String
    ): ByteArray {
        val isFromMe = currentUser?.id == userId

        if (!isFromMe && doubleRatchetEngine.isFirstTimeSeeingEphemeralPublicKey(ephemeralPublicKey)) {
            return doubleRatchetEngine.decryptMessage(
                ciphertext = ciphertext,
                remoteEphemeralPublicKey = ephemeralPublicKey,
                messageCounter = messageCounter,
                remoteDeviceId = deviceId,
            )
        }

        return doubleRatchetEngine.decryptStateless(
            ciphertext = ciphertext,
            ephemeralPublicKey = ephemeralPublicKey,
            messageCounter = messageCounter,
            isFromMe = isFromMe,
        )
    }

    fun setSelectedImageUri(uri: Uri?) {
        _state.update { it.copy(selectedImageUri = uri) }
    }

    fun setShowSelectedImagePopup(value: Boolean) {
        _state.update { it.copy(showSelectedImagePopup = value) }
    }

    fun setSelectedVideoUri(uri: Uri?) {
        _state.update { it.copy(selectedVideoUri = uri) }
    }

    fun setShowSelectedVideoPopup(value: Boolean) {
        _state.update { it.copy(showSelectedVideoPopup = value) }
    }

    suspend fun getAttachmentFileBytes(attachmentId: String): ByteArray {
        var fileBytes = "".toByteArray()
        attachmentRepository.getAttachmentById(attachmentId).onSuccess {
            fileBytes = it
        }
        return fileBytes
    }

    fun saveImageToGallery(imageUri: Uri) {
        fileManager.saveImageToGallery(imageUri)
    }

    fun saveVideoToGallery(videoUri: Uri) {
        fileManager.saveVideoToGallery(videoUri)
    }

    fun showImageOnFullScreen(imageUri: Uri) {
        _state.update {
            it.copy(
                selectedImageUri = imageUri,
                isFullScreenOpen = true,
            )
        }
    }

    fun showVideoOnFullScreen(videoUri: Uri) {
        _state.update {
            it.copy(
                selectedVideoUri = videoUri,
                isFullScreenOpen = true,
            )
        }
    }

    fun closeFullScreen() {
        _state.update {
            it.copy(
                selectedImageUri = null,
                selectedVideoUri = null,
                isFullScreenOpen = false,
            )
        }
    }

    suspend fun downloadFile(context: Context, message: ChatMessage) {
        attachmentRepository.getAttachmentById(message.attachment!!.id)
            .onSuccess { encryptedBytes ->
                val originalBytes = decryptMessage(
                    ciphertext = encryptedBytes,
                    ephemeralPublicKey = message.ephemeralPublicKey,
                    messageCounter = message.messageCounter,
                    userId = message.userId,
                    deviceId = message.deviceId
                )
                val filename = message.attachment.originalFileName
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI
                } else {
                    MediaStore.Files.getContentUri("external")
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(collectionUri, contentValues)

                uri?.let { targetUri ->
                    try {
                        resolver.openOutputStream(targetUri)?.use { outputStream ->
                            outputStream.write(originalBytes)
                        }
                        Toast.makeText(context, "File Saved", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        resolver.delete(targetUri, null, null)
                        e.printStackTrace()
                    }
                }
            }.onError { Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show() }
    }

    override fun onCleared() {
        super.onCleared()
        chatService.destroyChat()
    }
}