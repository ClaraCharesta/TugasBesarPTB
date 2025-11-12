package com.example.asramaku.pembayaran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.asramaku.model.PembayaranData
import com.example.asramaku.component.RiwayatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatPembayaranScreen(
    onBackClick: () -> Unit,
    riwayatList: List<PembayaranData>,
    onDetailClick: (Int) -> Unit
) {
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (riwayatList.isEmpty()) {
                Text("Belum ada riwayat pembayaran.")
            } else {
                LazyColumn {
                    itemsIndexed(riwayatList) { index, pembayaran ->
                        RiwayatCard(
                            bulanTagihan = pembayaran.bulan,
                            jumlahTagihan = pembayaran.totalTagihan,
                            status = pembayaran.status,
                            onDetailClick = { onDetailClick(index) }
                        )
                    }
                }
            }
        }
    }
}
