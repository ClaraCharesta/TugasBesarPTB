package com.example.asramaku.utils

import android.content.Context
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.asramaku.MyApp
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Buat file URI untuk kamera.
 * Dipakai oleh BuatLaporan / EditLaporan.
 * Pastikan authority FileProvider sesuai di AndroidManifest:
 * "${applicationId}.provider"
 */
fun createImageUri(context: Context): Uri? {
    return try {
        val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$time.jpg"
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (dir != null && !dir.exists()) dir.mkdirs()
        val imageFile = File(dir, fileName)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Utility untuk menyimpan Bitmap ke gallery
 * dan mengembalikan Uri gambar
 */
object ImageUtils {
    fun saveBitmapAndGetUri(bitmap: Bitmap): Uri? =
        runCatching {
            val resolver = MyApp.context.contentResolver

            val contentValues = ContentValues().apply {
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "bukti_${System.currentTimeMillis()}.jpg"
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }

            val uri = resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            uri?.let {
                val stream = resolver.openOutputStream(it)
                stream?.use { s ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, s)
                }
            }

            uri
        }.getOrNull() as Uri?
}
