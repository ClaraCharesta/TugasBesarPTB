package com.example.asramaku.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun uriToFile(uri: Uri, context: Context): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Cannot open input stream")

    val file = File.createTempFile("upload_", ".jpg", context.cacheDir)

    FileOutputStream(file).use { output ->
        inputStream.use { input ->
            input.copyTo(output)
        }
    }
    return file
}