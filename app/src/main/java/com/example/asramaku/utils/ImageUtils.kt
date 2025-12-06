package com.example.asramaku.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Buat file URI untuk kamera. Dipakai oleh BuatLaporan / EditLaporan.
 * Pastikan authority FileProvider sesuai di AndroidManifest: "${applicationId}.provider"
 */
fun createImageUri(context: Context): Uri? {
    return try {
        val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$time.jpg"
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (dir != null && !dir.exists()) dir.mkdirs()
        val imageFile = File(dir, fileName)
        FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
