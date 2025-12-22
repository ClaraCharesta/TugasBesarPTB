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

    // ===============================
    // 🔔 CHANNEL
    // ===============================
    private val PIKET_CHANNEL_ID = "piket_channel_v2"
    private val PAYMENT_CHANNEL_ID = "payment_channel"
    private val REPORT_CHANNEL_ID = "report_channel"

    private val PIKET_CHANNEL_NAME = "Reminder Piket"
    private val PAYMENT_CHANNEL_NAME = "Payment Notifications"
    private val REPORT_CHANNEL_NAME = "Report Notifications"

    // ===============================
    // 📩 TERIMA SEMUA FCM
    // ===============================
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("FCM_RECEIVED", "Pesan diterima: ${message.data}")

        val type = message.data["type"]

        when (type) {
            "PIKET" -> handlePiketNotification()
            "PAYMENT", "PAYMENT_REMINDER" -> handlePaymentNotification(message)
            "REPORT" -> handleReportNotification(message)
            else -> Log.d("FCM_RECEIVED", "Tipe notif tidak dikenali")
        }
    }

    // ===============================
    // 🔵 NOTIF PIKET (ASLI, TIDAK DIRUSAK)
    // ===============================
    private fun handlePiketNotification() {
        createPiketChannel()

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

        val notification = NotificationCompat.Builder(this, PIKET_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_duty)
            .setContentTitle("Reminder Piket")
            .setContentText("Jangan lupa cek jadwal piket hari ini yaa 🤍")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, "Notif PIKET diterima!", Toast.LENGTH_SHORT).show()
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)

        Log.d("FCM_NOTIF_DEBUG", "Notif PIKET tampil")
    }

    // ===============================
    // 💰 NOTIF PAYMENT (DIPERBAIKI, AMAN UNTUK DATA-ONLY & NOTIF)
    // ===============================
    private fun handlePaymentNotification(message: RemoteMessage) {
        createPaymentChannel()

        // Ambil title & body dari data dulu, fallback ke notification
        val title = message.data["title"] ?: message.notification?.title ?: "Tagihan Pembayaran"
        val body = message.data["body"] ?: message.notification?.body ?: "Ada tagihan yang belum dibayar"

        val notification = NotificationCompat.Builder(this, PAYMENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_payment)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)

        Log.d("FCM_PAYMENT", "Notif PAYMENT tampil")
    }

    // ===============================
    // 🧾 NOTIF REPORT (BARU, TANPA GANGGU YANG LAIN)
    // ===============================
    private fun handleReportNotification(message: RemoteMessage) {
        createReportChannel()

        val title = message.notification?.title ?: "Laporan Kerusakan"
        val body =
            message.notification?.body
                ?: "Apakah ada barang yang rusak? Silakan buat data laporan baru yaa 📝"

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, REPORT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_report)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)

        Log.d("FCM_REPORT", "Notif REPORT tampil")
    }

    // ===============================
    // 🔧 CHANNEL PIKET
    // ===============================
    private fun createPiketChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(PIKET_CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    PIKET_CHANNEL_ID,
                    PIKET_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel untuk pengingat jadwal piket"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 250, 500)
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    // ===============================
    // 🔧 CHANNEL PAYMENT
    // ===============================
    private fun createPaymentChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(PAYMENT_CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    PAYMENT_CHANNEL_ID,
                    PAYMENT_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel untuk notifikasi pembayaran"
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    // ===============================
    // 🔧 CHANNEL REPORT (BARU)
    // ===============================
    private fun createReportChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(REPORT_CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    REPORT_CHANNEL_ID,
                    REPORT_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel untuk notifikasi laporan kerusakan"
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    // ===============================
    // 🔑 TOKEN PIKET (TETAP)
    // ===============================
    override fun onNewToken(token: String) {
        Log.d("FCM_NEW_TOKEN_PIKET", "Token baru: $token")
        // token piket tetap seperti sebelumnya
    }
}
