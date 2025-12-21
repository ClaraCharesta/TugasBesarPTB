package com.example.asramaku.piket

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.asramaku.MainActivity
import com.example.asramaku.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PiketFirebaseMessagingService : FirebaseMessagingService() {

    private val CHANNEL_ID = "piket_channel_v2" // versi baru supaya pasti dibuat ulang
    private val CHANNEL_NAME = "Reminder Piket"

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("FCM_RECEIVED_PIKET", "Pesan diterima: ${message.data}")

        val type = message.data["type"]
        if (type != "PIKET") {
            Log.d("FCM_RECEIVED_PIKET", "Bukan notif PIKET, diabaikan")
            return
        }

        createChannel()

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_duty) // pakai ic_duty
            .setContentTitle("Reminder Piket")
            .setContentText("Jangan lupa cek jadwal piket hari ini yaa 🤍")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Toast aman di main thread
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, "Notif PIKET diterima!", Toast.LENGTH_SHORT).show()
        }

        Log.d("FCM_NOTIF_DEBUG", "Notification ready: ${notification.extras}")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
        Log.d("FCM_NOTIF_DEBUG", "Notif PIKET tampil")
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existingChannel = manager.getNotificationChannel(CHANNEL_ID)
            if (existingChannel == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel untuk pengingat jadwal piket"
                    enableLights(true)
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 250, 500)
                }
                manager.createNotificationChannel(channel)
                Log.d("FCM_NOTIF_DEBUG", "Channel PIKET dibuat (v2)")
            } else {
                Log.d("FCM_NOTIF_DEBUG", "Channel PIKET sudah ada (v2)")
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FCM_NEW_TOKEN_PIKET", "Token baru: $token")
        // Kirim token baru ke backend khusus PIKET
    }
}
