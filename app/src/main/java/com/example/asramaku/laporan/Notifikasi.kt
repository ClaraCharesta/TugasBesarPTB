package com.example.asramaku.laporan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun Notifikasi(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifikasi") },
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
        if (DummyData.daftarNotifikasi.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada notifikasi",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Riwayat Aktivitas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkTeal,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(DummyData.daftarNotifikasi) { notifikasi ->
                        NotifikasiCard(notifikasi = notifikasi)
                    }
                }
            }
        }
    }
}

@Composable
fun NotifikasiCard(notifikasi: Notifikasi) {
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
            Text(
                text = notifikasi.judul,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkTeal
            )

            Text(
                text = notifikasi.pesan,
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = notifikasi.waktu,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}