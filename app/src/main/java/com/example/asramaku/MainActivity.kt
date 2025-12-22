package com.example.asramaku

import android.annotation.SuppressLint
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

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val userId = getUserIdFromSession()
        if (userId == null) {
            Log.e("FCM_ERROR", "User belum login, tidak bisa kirim token FCM")
        } else {
            handleNotificationPermissionAndToken(userId)
        }


        setContent {
            val navController = rememberNavController()
            NavGraph(navController = navController)
        }
    }

    private fun handleNotificationPermissionAndToken(userId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            } else {
                sendFcmTokenAndSubscribe(userId)
            }
        } else {
            sendFcmTokenAndSubscribe(userId)
        }
    }

    private fun sendFcmTokenAndSubscribe(userId: Int) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM_TOKEN", "Token device: $token")


                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val request = FcmPiketRequest(userId, token)
                        FcmPiketService.api.sendToken(request)
                        Log.d("FCM", "✅ Token dikirim ke backend untuk user $userId")
                    } catch (e: Exception) {
                        Log.e("FCM_ERROR", "Gagal kirim token: ${e.message}")
                    }
                }


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

    private fun getUserIdFromSession(): Int? {
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        return if (prefs.contains("user_id")) prefs.getInt("user_id", -1) else null
    }

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
