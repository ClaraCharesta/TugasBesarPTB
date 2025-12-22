package com.example.asramaku.screens

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asramaku.R
import com.example.asramaku.navigation.Screen
import com.example.asramaku.data.preferences.UserPreferences
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import android.util.Log
import androidx.core.app.NotificationCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

@Composable
fun HomeScreen(
    navController: NavController,
    userName: String?,
    userId: Int
) {
    val backgroundColor = Color(0xFFFFE7C2)
    val moduleButtonColor = Color(0xFFB6DFD7)
    val moduleButtonColor2 = Color(0xFFD9ECE7)
    val textColor = Color(0xFF324E52)

    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val storedName = remember { userPrefs.getName() }

    val displayName = remember(userName, storedName) {
        when {
            !userName.isNullOrBlank() -> userName
            !storedName.isNullOrBlank() -> storedName
            else -> "User"
        }
    }

    var showHeader by remember { mutableStateOf(false) }
    var showLogo by remember { mutableStateOf(false) }
    var showModules by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        delay(150)
        showHeader = true
        delay(150)
        showLogo = true
        delay(200)
        showModules = true


        FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
            Log.d("FCM_HOME", "Token FCM Home: $fcmToken")
            sendHomeNotificationToken(userId, fcmToken)


            showLocalNotification(
                context,
                "Selamat Datang",
                "Halo $displayName, selamat datang di Asramaku!, jangan lupa kerjakan jadwal piket yang telah tersedia"
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {


            AnimatedVisibility(
                visible = showHeader,
                enter = fadeIn() + expandVertically()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hello, $displayName!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    IconButton(onClick = { /* TODO profile */ }) {
                        Image(
                            painter = painterResource(R.drawable.ic_profile),
                            contentDescription = "Profile",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))


            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD7E7E1), RoundedCornerShape(16.dp))
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.school_icon),
                        contentDescription = "Asramaku Logo",
                        modifier = Modifier.size(120.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "ASRAMAKU",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }

            Spacer(Modifier.height(32.dp))


            AnimatedVisibility(
                visible = showModules,
                enter = fadeIn() + slideInVertically { it / 3 }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {


                    ModuleItem(
                        title = "MODUL PEMBAYARAN",
                        iconRes = R.drawable.ic_payment,
                        backgroundColor = moduleButtonColor,
                        textColor = textColor
                    ) {
                        navController.navigate(Screen.Payment.createRoute(userId))
                    }


                    ModuleItem(
                        title = "MODUL PELAPORAN KERUSAKAN BARANG",
                        iconRes = R.drawable.ic_report,
                        backgroundColor = moduleButtonColor2,
                        textColor = textColor
                    ) {
                        navController.navigate(Screen.Report.createRoute(userId, displayName ?: "User"))
                    }


                    ModuleItem(
                        title = "MODUL PIKET",
                        iconRes = R.drawable.ic_duty,
                        backgroundColor = moduleButtonColor,
                        textColor = textColor
                    ) {
                        navController.navigate(
                            Screen.Duty.createRoute(
                                userId,
                                displayName ?: "User"
                            )
                        )
                    }

                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun ModuleItem(
    title: String,
    iconRes: Int,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = textColor
            )
        }
    }
}


private fun sendHomeNotificationToken(userId: Int, token: String) {
    val json = JSONObject().apply {
        put("userId", userId)
        put("fcmToken", token)
    }

    val body = json.toString()
        .toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url("http://10.0.2.2:3000/api/fcm/home/token")
        .post(body) // <-- POST request
        .build()

    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("FCM_HOME_FAIL", "Gagal kirim token: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            Log.d("FCM_HOME_OK", "Token Home terkirim, response code: ${response.code}")
        }
    })

}


private fun showLocalNotification(context: android.content.Context, title: String, message: String) {
    val channelId = "home_channel"
    val notificationManager =
        context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Home Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_duty)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(1001, notification)
}
