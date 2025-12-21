package com.example.asramaku

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.asramaku.data.remote.FcmPiketRequest
import com.example.asramaku.data.remote.FcmPiketService
import com.example.asramaku.navigation.NavGraph
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val CHANNEL_ID = "piket_channel"

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1️⃣ Buat channel notifikasi Android 8+
        createNotificationChannel()

        // 2️⃣ Ambil userId dari session / login
        val userId = getUserIdFromSession()
        if (userId == null) {
            Log.e("FCM_ERROR", "User belum login, tidak bisa kirim token FCM")
        } else {
            // 2a️⃣ Minta permission POST_NOTIFICATIONS untuk Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                    android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
                } else {
                    // Permission sudah ada, kirim token
                    sendFcmTokenAndSubscribe(userId)
                }
            } else {
                // Android < 13
                sendFcmTokenAndSubscribe(userId)
            }
        }

        // 3️⃣ Set UI / Navigation
        setContent {
            val navController = rememberNavController()
            NavGraph(navController = navController)
        }
    }

    // 🔹 Ambil token FCM, kirim ke backend, dan subscribe ke topic "piket"
    private fun sendFcmTokenAndSubscribe(userId: Int) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM_TOKEN", "Token device: $token")

                // Kirim token ke backend
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val request = FcmPiketRequest(userId, token)
                        FcmPiketService.api.sendToken(request)
                        Log.d("FCM", "✅ Token dikirim ke backend untuk user $userId")
                    } catch (e: Exception) {
                        Log.e("FCM_ERROR", "Gagal kirim token: ${e.message}")
                    }
                }

                // Subscribe ke topic "piket"
                FirebaseMessaging.getInstance().subscribeToTopic("piket")
                    .addOnCompleteListener { subTask ->
                        if (subTask.isSuccessful) {
                            Log.d("FCM_TOPIC", "Berhasil subscribe topic piket")
                        } else {
                            Log.e("FCM_TOPIC", "Gagal subscribe topic: ${subTask.exception?.message}")
                        }
                    }
            } else {
                Log.e("FCM_ERROR", "Gagal ambil token FCM")
            }
        }
    }

    // 🔹 Ambil userId dari SharedPreferences (session)
    private fun getUserIdFromSession(): Int? {
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        return if (prefs.contains("user_id")) prefs.getInt("user_id", -1) else null
    }

    // 🔹 Buat notification channel dengan bunyi & vibrate
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Piket Notification",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
            }
            manager.createNotificationChannel(channel)
        }
    }

    // 🔹 Handle hasil permission Android 13+
    @Deprecated("Gunakan Activity Result API")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() &&
            grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            val userId = getUserIdFromSession()
            if (userId != null) sendFcmTokenAndSubscribe(userId)
        }
    }
}
