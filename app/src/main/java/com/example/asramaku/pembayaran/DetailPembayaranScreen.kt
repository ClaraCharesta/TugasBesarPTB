package com.example.asramaku.pembayaran

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.asramaku.model.PembayaranData


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPembayaranScreen(
    pembayaran: PembayaranData,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pembayaran") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFFA8C5C2)
                )
            )
        },
        containerColor = Color(0xFFFCEED8) // background krem
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD5E6E0) // hijau muda
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text("Nama : ${pembayaran.nama}", fontSize = 16.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("No. Kamar : ${pembayaran.noKamar}", fontSize = 16.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Bulan Tagihan : ${pembayaran.bulan}", fontSize = 16.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Jumlah : Rp ${pembayaran.totalTagihan}", fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Bukti Pembayaran :", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    if (!pembayaran.buktiUri.isNullOrEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(pembayaran.buktiUri),
                            contentDescription = "Bukti Pembayaran",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(
                                    Color(0xFFA8C5C2),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(
                                    Color(0xFFA8C5C2),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Tidak ada bukti foto", color = Color.DarkGray)
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Row {
                        Text("Status Pembayaran: ", fontSize = 16.sp)
                        Text(
                            text = pembayaran.status,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
