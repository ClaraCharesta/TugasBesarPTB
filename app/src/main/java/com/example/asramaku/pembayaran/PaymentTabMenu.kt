package com.example.asramaku.pembayaran

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PaymentTabMenu(
    currentRoute: String,
    navController: NavController,
    userId: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3E8FF))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {


        PaymentTabItem(
            label = "Tagihan",
            icon = Icons.Default.List,
            selected = currentRoute.startsWith("payment_screen")
        ) {
            navController.navigate("payment_screen/$userId") {
                popUpTo("payment_screen/$userId") { inclusive = true }
                launchSingleTop = true
            }
        }


        PaymentTabItem(
            label = "Status",
            icon = Icons.Default.CheckCircle,
            selected = currentRoute.startsWith("status_pembayaran")
        ) {
            if (!currentRoute.startsWith("status_pembayaran")) {
                navController.navigate("status_pembayaran/$userId") {
                    popUpTo("payment_screen/$userId") { inclusive = false }
                    launchSingleTop = true
                }
            }
        }



        PaymentTabItem(
            label = "Riwayat",
            icon = Icons.Default.History,
            selected = currentRoute.startsWith("riwayat_pembayaran")
        ) {
            navController.navigate("riwayat_pembayaran/$userId") {
                launchSingleTop = true
            }
        }
    }
}

@Composable
fun PaymentTabItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .background(
                    color = if (selected) Color(0xFFE7D5F9) else Color.Transparent,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
                .padding(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.Black
            )
        }

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Black
        )
    }
}
