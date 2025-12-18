package com.example.asramaku.pembayaran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.asramaku.component.RiwayatCard
import com.example.asramaku.data.session.UserSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatPembayaranScreen(
    navController: NavController,
    viewModel: PaymentViewModel,
    onDetailClick: (Int) -> Unit = {},
    onDeleteItem: (Int) -> Unit
) {

    // =========================
    // USER LOGIN
    // =========================
    val userId = UserSession.userId

    // =========================
    // LOAD DATA RIWAYAT (LUNAS)
    // =========================
    LaunchedEffect(userId) {
        userId?.let {
            viewModel.loadRiwayatLunas(it)
        }
    }

    // =========================
    // OBSERVE DATA
    // =========================
    val riwayatLunas by viewModel.riwayatLunasList.collectAsState()

    // =========================
    // STATE DIALOG HAPUS
    // =========================
    var showDialog by remember { mutableStateOf(false) }
    var paymentIdToDelete by remember { mutableStateOf<Int?>(null) }

    // =========================
    // DIALOG KONFIRMASI
    // =========================
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus Riwayat?") },
            text = {
                Text("Apakah Anda yakin ingin menghapus riwayat pembayaran ini?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        paymentIdToDelete?.let { id ->
                            onDeleteItem(id)   // 🔥 KIRIM ID DATABASE
                        }
                        showDialog = false
                    }
                ) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // =========================
    // UI
    // =========================
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Pembayaran") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFA8C9C4),
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            PaymentTabMenu(
                currentRoute = "riwayat_pembayaran",
                navController = navController
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(Color(0xFFFFEED1))
                .fillMaxSize()
                .padding(16.dp)
        ) {

            if (riwayatLunas.isEmpty()) {
                Text("Belum ada riwayat pembayaran.")
            } else {
                LazyColumn {
                    items(riwayatLunas) { item ->
                        RiwayatCard(
                            bulanTagihan = item.bulan,
                            jumlahTagihan = item.totalTagihan.toString(),
                            status = item.status,
                            onDetailClick = {
                                onDetailClick(item.id)
                            },
                            onDeleteClick = {
                                paymentIdToDelete = item.id   // ✅ ID ASLI
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}
