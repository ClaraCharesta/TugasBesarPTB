package com.example.asramaku.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.asramaku.data.model.Payment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@Composable
fun PaymentModuleScreen(
    navController: NavController,
    viewModel: PaymentViewModel,
    userId: Int
) {
    val tagihanList by viewModel.tagihanList.collectAsState()


    LaunchedEffect(userId) {
        if (userId != 0) {

            viewModel.loadTagihan(userId)


            snapshotFlow { tagihanList }
                .distinctUntilChanged()
                .collectLatest { list ->
                    val hasPending = list.any { it.status.lowercase() != "lunas" }
                    if (hasPending) {
                        sendPaymentReminder(userId)
                        android.util.Log.d(
                            "FCM_REMINDER",
                            "Ada tagihan pending, mengirim notifikasi"
                        )
                    } else {
                        android.util.Log.d(
                            "FCM_REMINDER",
                            "Tidak ada tagihan pending, tidak mengirim notifikasi"
                        )
                    }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0D5))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFA7D7C5))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Lihat Tagihan Pembayaran",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        ) {
            if (tagihanList.isEmpty()) {
                Text("Tidak ada tagihan.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tagihanList) { item ->
                        TagihanCard(
                            payment = item,
                            onBayarClick = {
                                navController.navigate(
                                    "konfirmasi_pembayaran/${userId}/${item.bulan}/${item.totalTagihan}"
                                )
                            }
                        )
                    }
                }
            }
        }


        BottomNavigationBar(navController, userId)
    }
}


private fun sendPaymentReminder(userId: Int) {
    val client = OkHttpClient.Builder()
        .callTimeout(60, TimeUnit.SECONDS)
        .build()

    val json = org.json.JSONObject().apply { put("userId", userId) }
    val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    val request = Request.Builder()
        .url("http://10.0.2.2:3000/api/fcm/payment/reminder")


        .post(body)
        .build()


    client.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
            android.util.Log.e("FCM_REMINDER", "Exception: ${e.message}")
        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
            if (response.isSuccessful) {
                android.util.Log.d("FCM_REMINDER", "✅ Reminder dikirim")
            } else {
                android.util.Log.e("FCM_REMINDER", "❌ Gagal kirim reminder: ${response.message}")
            }
        }
    })
}

@Composable
fun TagihanCard(
    payment: Payment,
    onBayarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color(0xFFCFE8D5)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bulan tagihan : ${payment.bulan}",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Total Tagihan : Rp.${payment.totalTagihan}"
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val isDisabled = payment.buktiBayar != null

                Button(
                    onClick = onBayarClick,
                    enabled = !isDisabled
                ) {
                    Text(
                        if (isDisabled) "Menunggu Konfirmasi"
                        else "Bayar Sekarang"
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    userId: Int
) {
    NavigationBar(containerColor = Color(0xFFF3E6F7)) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.List, contentDescription = "Tagihan") },
            label = { Text("Tagihan") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("status_pembayaran/$userId") },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Status") },
            label = { Text("Status") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("riwayat_pembayaran/$userId") },
            icon = { Icon(Icons.Default.History, contentDescription = "Riwayat") },
            label = { Text("Riwayat") }
        )
    }
}
