package com.example.asramaku.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun RekeningCard(bank: String, nomor: String, nama: String, logo: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCE8DC)),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = logo),
                contentDescription = bank,
                modifier = Modifier.size(40.dp)
            )
            Column {
                Text(
                    text = "$bank  $nomor  a.n $nama",
                    color = Color(0xFF2E6664)
                )
            }
        }
    }
}
