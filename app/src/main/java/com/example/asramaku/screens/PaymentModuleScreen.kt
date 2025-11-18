package com.example.asramaku.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.asramaku.component.TagihanCard
import com.example.asramaku.pembayaran.PaymentTabMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentModuleScreen(
    navController: NavController,
    daftarTagihan: List<String> // 🟢 tambahan agar data bisa direaktifkan
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lihat Tagihan Pembayaran") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFAED6D3),
                    titleContentColor = Color.Black
                )
            )
        },

        bottomBar = {
            // bottom bar hanya dipasang bila navController tersedia (di sini selalu ada)
            PaymentTabMenu(
                currentRoute = "daftar_tagihan",
                navController = navController
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFFFF0D5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val nama = "Asyifa"
            val noKamar = "A203"
            val totalTagihan = "500000"

            // 🟢 Sekarang daftarTagihan berasal dari parameter
            if (daftarTagihan.isEmpty()) {
                Text("Semua tagihan sudah lunas 🎉", color = Color.DarkGray)
            } else {
                daftarTagihan.forEach { bulan ->
                    TagihanCard(
                        bulan = bulan,
                        nama = nama,
                        noKamar = noKamar,
                        totalTagihan = "Rp.$totalTagihan",
                        onBayarClick = {
                            navController.navigate(
                                "konfirmasi_pembayaran/${bulan}/${nama}/${noKamar}/${totalTagihan}"
                            )
                        }
                    )
                }
            }
        }
    }
}

// biar kompatibel dengan navGraph lama
@Composable
fun PaymentScreen(navController: NavController) {
    // Hanya navigate ke daftar_tagihan supaya memakai state dari NavGraph.
    navController.navigate("daftar_tagihan")
}
