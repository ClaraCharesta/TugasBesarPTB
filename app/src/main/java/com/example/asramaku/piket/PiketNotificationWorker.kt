package com.example.asramaku.piket



import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class PiketNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val userName = inputData.getString("userName") ?: "User"
        val tanggalPiket = inputData.getString("tanggal") ?: "Hari ini"
        val status = inputData.getString("status") ?: "Belum Dikerjakan"

        showNotification(userName, tanggalPiket, status)
        return Result.success()
    }

    private fun showNotification(userName: String, tanggal: String, status: String) {
        val channelId = "piket_channel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Buat channel (Android O ke atas)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Piket Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notifikasi untuk jadwal piket"
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Piket: $status")
            .setContentText("Hai $userName, jadwal piketmu tanggal $tanggal belum selesai!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(tanggal.hashCode(), builder.build())
    }
}
