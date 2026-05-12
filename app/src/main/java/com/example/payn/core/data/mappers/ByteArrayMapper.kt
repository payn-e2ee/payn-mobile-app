package com.example.payn.core.data.mappers

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import java.io.File
import java.util.UUID

fun ByteArray.toUri(context: Context, fileName: String): Uri {
    val tempFile = File.createTempFile(fileName, null, context.cacheDir)
    tempFile.outputStream().use {
        it.write(this)
    }
    return tempFile.toUri()
}

fun ByteArray.toUri(context: Context): Uri {
    val fileName = UUID.randomUUID().toString()
    val tempFile = File.createTempFile(fileName, null, context.cacheDir)
    tempFile.outputStream().use {
        it.write(this)
    }
    return tempFile.toUri()
}