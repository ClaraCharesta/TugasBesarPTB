package com.example.asramaku.laporan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asramaku.model.DummyData
import com.example.asramaku.model.Laporan
import com.example.asramaku.model.Notifikasi
import com.example.asramaku.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarLaporan(navController: NavController) {
    var laporanToDelete by remember { mutableStateOf<Laporan?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Laporan Saya") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightYellow
                )
            )
        },
        containerColor = LightYellow
    ) { paddingValues ->
        if (DummyData.daftarLaporan.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada laporan",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(DummyData.daftarLaporan) { laporan ->
                    LaporanCard(
                        laporan = laporan,
                        onDetailClick = {
                            navController.navigate("detail_laporan/${laporan.id}")
                        },
                        onEditClick = {
                            navController.navigate("edit_laporan/${laporan.id}")
                        },
                        onDeleteClick = {
                            laporanToDelete = laporan
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && laporanToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Apakah kamu yakin\nmenghapus laporan ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        laporanToDelete?.let { laporan ->
                            DummyData.daftarLaporan.remove(laporan)

                            // Tambah notifikasi
                            val newNotifikasi = Notifikasi(
                                id = (DummyData.daftarNotifikasi.size + 1).toString(),
                                judul = "Laporan dihapus",
                                pesan = "Laporan '${laporan.judulKerusakan}' telah dihapus",
                                waktu = "Baru saja",
                                tipe = "hapus"
                            )
                            DummyData.daftarNotifikasi.add(0, newNotifikasi)
                        }
                        showDeleteDialog = false
                        laporanToDelete = null
                    }
                ) {
                    Text("Ya", color = DarkTeal)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    laporanToDelete = null
                }) {
                    Text("Tidak", color = RedButton)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun LaporanCard(
    laporan: Laporan,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightTeal
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = laporan.judulKerusakan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkTeal,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (laporan.status) {
                        "Menunggu" -> Color(0xFFFFE5E5)
                        "Diproses" -> Color(0xFFFFF3E0)
                        else -> Color(0xFFE8F5E9)
                    }
                ) {
                    Text(
                        text = laporan.status,
                        fontSize = 12.sp,
                        color = when (laporan.status) {
                            "Menunggu" -> RedButton
                            "Diproses" -> Color(0xFFF57C00)
                            else -> Color(0xFF4CAF50)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = laporan.tanggal,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDetailClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkTeal
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Lihat", fontSize = 14.sp)
                }

                Button(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightYellow
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Edit", fontSize = 14.sp, color = DarkTeal)
                }

                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedButton
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Hapus", fontSize = 14.sp)
                }
            }
        }
    }
}