package com.example.asramaku.pembayaran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.asramaku.model.PembayaranData
import com.example.asramaku.component.RiwayatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatPembayaranScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    riwayatList: List<PembayaranData>,
    onDetailClick: (Int) -> Unit,
    onDeleteItem: (Int) -> Unit
) {
    // ⭐ STATE untuk daftar yang bisa dihapus
    var list by remember { mutableStateOf(riwayatList) }

    // ⭐ STATE dialog konfirmasi
    var showDialog by remember { mutableStateOf(false) }
    var indexToDelete by remember { mutableStateOf(-1) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus Riwayat?") },
            text = { Text("Apakah Anda yakin ingin menghapus riwayat pembayaran ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteItem(indexToDelete)
                        showDialog = false
                    }
                ) { Text("Hapus", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Pembayaran") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
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
            if (list.isEmpty()) {
                Text("Belum ada riwayat pembayaran.")
            } else {
                LazyColumn {
                    itemsIndexed(list) { index, pembayaran ->
                        RiwayatCard(
                            bulanTagihan = pembayaran.bulan,
                            jumlahTagihan = pembayaran.totalTagihan,
                            status = pembayaran.status,
                            onDetailClick = { onDetailClick(index) },

                            // fitur hapus
                            onDeleteClick = {
                                indexToDelete = index
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}
