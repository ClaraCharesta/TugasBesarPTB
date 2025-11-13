package com.example.asramaku.laporan

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.asramaku.model.DummyData
import com.example.asramaku.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailLaporan(navController: NavController, laporanId: String) {
    val laporan = DummyData.daftarLaporan.find { it.id == laporanId }

    if (laporan == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Laporan tidak ditemukan")
        }
        return
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
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
                            fontSize = 20.sp,
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

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Deskripsi Kerusakan:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkTeal
                    )
                    Text(
                        text = laporan.deskripsiKerusakan,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Lokasi/Kamar:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkTeal
                    )
                    Text(
                        text = laporan.lokasiKamar,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Foto:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkTeal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (laporan.fotoUrl != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(Uri.parse(laporan.fotoUrl)),
                                contentDescription = "Foto Kerusakan",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.PhotoLibrary,
                                        contentDescription = "Tidak ada foto",
                                        modifier = Modifier.size(48.dp),
                                        tint = Color.LightGray
                                    )
                                    Text(
                                        text = "Tidak ada foto",
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}