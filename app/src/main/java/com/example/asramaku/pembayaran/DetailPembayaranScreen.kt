package com.example.asramaku.pembayaran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.asramaku.screens.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPembayaranScreen(
    navController: NavController,
    paymentId: Int,
    userId: Int,
    viewModel: PaymentViewModel,
    onBackClick: () -> Unit
) {


    val detailLiveData = remember(paymentId) {
        viewModel.getDetailPayment(paymentId)
    }

    val pembayaran by detailLiveData.observeAsState()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(pembayaran) {
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pembayaran", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB7D7CF)
                )
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF1D6))
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                when {
                    isLoading -> {
                        CircularProgressIndicator()
                    }

                    pembayaran != null -> {
                        val data = pembayaran!!

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFD8EAD7)
                            )
                        ) {
                            Column(Modifier.padding(16.dp)) {

                                Text("Bulan: ${data.bulan}")
                                Text("Total: Rp ${data.totalTagihan}")

                                Row {
                                    Text("Status: ")
                                    Text(
                                        data.status,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }

                                Spacer(Modifier.height(12.dp))
                                Text("Bukti Pembayaran")

                                Spacer(Modifier.height(8.dp))

                                if (!data.buktiBayar.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = "http://10.0.2.2:3000${data.buktiBayar}",
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text("Bukti pembayaran tidak tersedia")
                                }
                            }
                        }
                    }

                    else -> {
                        Text(
                            "Data pembayaran tidak ditemukan",
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}