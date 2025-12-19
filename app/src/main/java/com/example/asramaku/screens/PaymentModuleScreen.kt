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


@Composable
fun PaymentModuleScreen(
    navController: NavController,
    viewModel: PaymentViewModel,
    userId: Int // 🔹 ambil userId dari parameter
) {

    val tagihanList by viewModel.tagihanList.collectAsState()

    LaunchedEffect(userId) {
        if (userId != 0) {
            viewModel.loadTagihan(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0D5))
    ) {

        // =========================
        // TOP BAR (PANAH BACK)
        // =========================
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

        // =========================
        // LIST TAGIHAN
        // =========================
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

        // =========================
        // BOTTOM NAVIGATION
        // =========================
        BottomNavigationBar(
            navController = navController,
            userId = userId
        )

    }
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
    NavigationBar(
        containerColor = Color(0xFFF3E6F7)
    ) {

        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.List, contentDescription = "Tagihan") },
            label = { Text("Tagihan") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("status_pembayaran/$userId")
            },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Status") },
            label = { Text("Status") }
        )



        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("riwayat_pembayaran/$userId")
            },
            icon = { Icon(Icons.Default.History, contentDescription = "Riwayat") },
            label = { Text("Riwayat") }
        )

    }
}
