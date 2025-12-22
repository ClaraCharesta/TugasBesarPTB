package com.example.asramaku.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asramaku.ui.theme.DarkTeal
import com.example.asramaku.ui.theme.LightYellow
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    userId: Int,
    userName: String?
) {
    LaunchedEffect(Unit) {
        if (userId != 0) {
            sendReportNotification(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Laporan Kehilangan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkTeal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightYellow,
                )
            )
        },
        containerColor = LightYellow
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Silakan laporkan jika terjadi kerusakan, terima kasih",
                fontSize = 14.sp,
                color = DarkTeal
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuCard(
                title = "Buat Laporan Baru",
                icon = Icons.Default.Add,
                onClick = { navController.navigate("buat_laporan") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuCard(
                title = "Daftar Laporan",
                icon = Icons.Default.List,
                onClick = { navController.navigate("daftar_laporan") }
            )
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkTeal)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = title, tint = Color.White)
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, color = Color.White)
            }
            Text("Buka", color = LightYellow)
        }
    }
}

private fun sendReportNotification(userId: Int) {
    val json = JSONObject().apply {
        put("userId", userId)
    }

    val body = json.toString()
        .toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url("http://10.0.2.2:3000/api/fcm/report/send")
        .post(body)
        .build()

    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("FCM_REPORT", "Gagal kirim notif report: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                Log.d("FCM_REPORT", "✅ Notifikasi report dikirim")
            } else {
                Log.e("FCM_REPORT", "❌ Gagal kirim notif report: ${response.message}")
            }
        }
    })
}
