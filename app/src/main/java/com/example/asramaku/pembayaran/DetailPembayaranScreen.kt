package com.example.asramaku.pembayaran

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPembayaranScreen(
    paymentId: Int,
    viewModel: PaymentViewModel,
    onBackClick: () -> Unit
) {

    // ================= LOAD DATA (TIDAK DIUBAH) =================
    LaunchedEffect(paymentId) {
        viewModel.loadPaymentDetail(paymentId)
    }

    val pembayaran by viewModel.detailPayment.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Pembayaran",
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB7D7CF) // ✅ warna hijau mint seperti foto
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF1D6)) // 🌼 background cream
                .padding(paddingValues)
        ) {

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                pembayaran != null -> {
                    val data = pembayaran!!

                    // ================= LOG DEBUG (TIDAK DIUBAH) =================
                    LaunchedEffect(data.buktiBayar) {
                        Log.d("BUKTI_BAYAR", data.buktiBayar ?: "NULL")
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFD8EAD7) // 🌿 hijau pastel
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {

                            Text(
                                text = "Bulan: ${data.bulan}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Total: Rp ${data.totalTagihan}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row {
                                Text(
                                    text = "Status Pembayaran: ",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = data.status,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32) // hijau "Lunas"
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Bukti Pembayaran :",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // ================= FOTO BUKTI BAYAR (TIDAK DIUBAH) =================
                            if (!data.buktiBayar.isNullOrEmpty()) {

                                val imageUrl =
                                    "http://10.0.2.2:3000${data.buktiBayar}"

                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Bukti Pembayaran",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .background(
                                            color = Color(0xFF9ED0C3),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentScale = ContentScale.Crop
                                )

                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .background(
                                            color = Color(0xFF9ED0C3),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Bukti pembayaran tidak tersedia",
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    Text(
                        text = "Data pembayaran tidak ditemukan",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
