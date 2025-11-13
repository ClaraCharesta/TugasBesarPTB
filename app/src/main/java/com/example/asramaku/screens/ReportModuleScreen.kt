package com.example.asramaku.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asramaku.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Layar Utama",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightYellow,
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
        ) {
            Text(
                text = "Hallo, Reva!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkTeal
            )
            Text(
                text = "Silakan laporkan jika terjadi kerusakan, terima kasih",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuCard(
                title = "Buat Laporan Baru",
                icon = Icons.Default.Add,
                onClick = { navController.navigate("buat_laporan") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuCard(
                title = "Daftar Laporan",
                icon = Icons.Default.List,
                onClick = { navController.navigate("daftar_laporan") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuCard(
                title = "Notifikasi",
                icon = Icons.Default.Notifications,
                onClick = { navController.navigate("notifikasi") }
            )
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkTeal
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
            Text(
                text = "Buka",
                fontSize = 14.sp,
                color = LightYellow
            )
        }
    }
}
