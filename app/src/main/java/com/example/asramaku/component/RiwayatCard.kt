package com.example.asramaku.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RiwayatCard(
    bulanTagihan: String,
    jumlahTagihan: String,
    status: String,
    onDetailClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCE8DC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Bulan tagihan : $bulanTagihan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Jumlah tagihan : Rp.$jumlahTagihan",
                fontSize = 16.sp
            )
            Text(
                text = "Status pembayaran : $status",
                fontSize = 16.sp,
                color = if (status == "Lunas") Color(0xFF2E7D32) else Color(0xFFD32F2F)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onDetailClick,
                modifier = Modifier.align(Alignment.Start),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E6664))
            ) {
                Text("Lihat detail")
            }
        }
    }
}
