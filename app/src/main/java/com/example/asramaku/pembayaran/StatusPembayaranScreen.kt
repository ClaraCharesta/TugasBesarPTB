package com.example.asramaku.pembayaran

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asramaku.component.StatusPembayaranRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusPembayaranScreen(
    navController: NavController? = null,
    riwayatList: List<Triple<String, String, String>> = emptyList()
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // ===== KONTEN LAMA (UTUH) =====
        Box(modifier = Modifier.weight(1f)) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Status Pembayaran",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        },
                        navigationIcon = {},
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFFAED6D3)
                        )
                    )
                },
                containerColor = Color(0xFFFFF0D5)
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFC9D6C3))
                            .border(1.dp, Color.DarkGray),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableHeaderCell("No", 0.15f)
                        TableHeaderCell("Bulan Tagihan", 0.45f)
                        TableHeaderCell("Status Pembayaran", 0.4f)
                    }

                    val dataPembayaran = if (riwayatList.isNotEmpty()) {
                        riwayatList.mapIndexed { index, triple ->
                            Triple(index + 1, triple.first, triple.third)
                        }
                    } else {
                        listOf(
                            Triple(1, "Oktober", "Lunas"),
                            Triple(2, "November", "Lunas"),
                            Triple(3, "Desember", "Belum Lunas")
                        )
                    }

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        itemsIndexed(dataPembayaran) { _, (no, bulan, status) ->
                            StatusPembayaranRow(no, bulan, status)
                        }
                    }
                }
            }
        }

        // ===== TAB MENU PEMBAYARAN (BARU) =====
        PaymentTabMenu(
            currentRoute = "status_pembayaran",
            navController = navController!!
        )
    }
}

@Composable
fun RowScope.TableHeaderCell(text: String, weight: Float) {
    Box(
        modifier = Modifier
            .weight(weight)
            .background(Color((0xFFD9E1D0)))
            .border(1.dp, Color.Black)
            .height(IntrinsicSize.Min),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
