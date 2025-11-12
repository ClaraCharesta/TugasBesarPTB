package com.example.asramaku.component

import androidx.compose.foundation.background
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
fun TagihanCard(
    bulan: String,
    nama: String,
    noKamar: String,
    totalTagihan: String,
    onBayarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCE8DC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                "Bulan Tagihan : $bulan",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(Modifier.height(4.dp))
            Text("Nama : $nama")
            Text("No. Kamar : $noKamar")
            Text("Total Tagihan : $totalTagihan")

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onBayarClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .widthIn(min = 150.dp)
                    .height(42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E6664)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Bayar Sekarang", fontSize = 14.sp, color = Color.White)
            }
        }
    }
}
