package com.example.payn.core.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast

class FileManager(private val context: Context) {

    fun readBytesFromUri(uri: Uri): ByteArray? {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        }
    }

    fun getFileName(uri: Uri): String {
        var name = ""
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                name = cursor.getString(nameIndex)
            }
        }
        return name.ifEmpty { uri.path?.substringAfterLast('/') ?: "unknown_file" }
    }

    fun getFileSize(uri: Uri): Long {
        var size = 0L
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst() && sizeIndex != -1) {
                size = cursor.getLong(sizeIndex)
            }
        }
        return size
    }

    fun saveImageToGallery(imageUri: Uri) {
        val imageBytes = readBytesFromUri(imageUri)
        val filename = "IMAGE_${System.currentTimeMillis()}"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Payn")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { targetUri ->
            try {
                resolver.openOutputStream(targetUri)?.use { outputStream ->
                    outputStream.write(imageBytes)
                }
                Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                resolver.delete(targetUri, null, null)
                e.printStackTrace()
            }
        }
    }

    fun saveVideoToGallery(videoUri: Uri) {
        val videoBytes = readBytesFromUri(videoUri)
        val filename = "VIDEO_${System.currentTimeMillis()}"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/Payn")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { targetUri ->
            try {
                resolver.openOutputStream(targetUri)?.use { outputStream ->
                    outputStream.write(videoBytes)
                }
                Toast.makeText(context, "Video Saved", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                resolver.delete(targetUri, null, null)
                e.printStackTrace()
            }
        }
    }
}