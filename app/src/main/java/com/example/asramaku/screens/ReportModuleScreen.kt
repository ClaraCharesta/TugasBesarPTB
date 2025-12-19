package com.example.asramaku.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asramaku.navigation.Screen
import com.example.asramaku.ui.theme.*
import com.example.asramaku.data.preferences.UserPreferences
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    userName: String?
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val storedName = userPrefs.getName()

    // pilih nama mana yang dipakai
    val displayName = when {
        !userName.isNullOrBlank() -> userName
        !storedName.isNullOrBlank() -> storedName
        else -> "User"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Laporan Kehilangan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkTeal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Gray
                        )
                    }
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
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Silakan laporkan jika terjadi kerusakan, terima kasih",
                fontSize = 14.sp,
                color = DarkTeal,
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
