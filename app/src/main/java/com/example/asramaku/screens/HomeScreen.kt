package com.example.asramaku.screens

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asramaku.R
import com.example.asramaku.navigation.Screen
import kotlinx.coroutines.delay
import com.example.asramaku.data.preferences.UserPreferences

@Composable
fun HomeScreen(navController: NavController, userName: String?, userId: Int) {
    val backgroundColor = Color(0xFFFFE7C2)
    val moduleButtonColor = Color(0xFFB6DFD7)
    val moduleButtonColor2 = Color(0xFFD9ECE7)
    val textColor = Color(0xFF324E52)

    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val storedName = remember { userPrefs.getName() } // bisa null
    val displayName = remember(userName, storedName) {
        when {
            !userName.isNullOrBlank() -> userName!!
            !storedName.isNullOrBlank() -> storedName!!
            else -> "User"
        }
    }

    var showHeader by remember { mutableStateOf(false) }
    var showLogo by remember { mutableStateOf(false) }
    var showModules by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150)
        showHeader = true
        delay(150)
        showLogo = true
        delay(200)
        showModules = true
    }

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top
        ) {
            // Header
            AnimatedVisibility(
                visible = showHeader,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hello, $displayName!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    IconButton(onClick = { /* TODO: navigasi ke profil */ }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile),
                            contentDescription = "Profile",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logo
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD7E7E1), RoundedCornerShape(16.dp))
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.school_icon),
                        contentDescription = "Asramaku Logo",
                        modifier = Modifier.size(120.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "ASRAMAKU",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Modul Buttons
            AnimatedVisibility(
                visible = showModules,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
                exit = fadeOut()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ModuleItem(
                        title = "MODUL PEMBAYARAN",
                        iconRes = R.drawable.ic_payment,
                        backgroundColor = moduleButtonColor,
                        textColor = textColor
                    ) {
                        navController.navigate(Screen.Payment.createRoute(userId))
                    }

                    ModuleItem(
                        title = "MODUL  PELAPORAN KERUSAKAN BARANG",
                        iconRes = R.drawable.ic_report,
                        backgroundColor = moduleButtonColor2,
                        textColor = textColor
                    ) {
                        navController.navigate(Screen.Report.route)
                    }

                    ModuleItem(
                        title = "MODUL PIKET",
                        iconRes = R.drawable.ic_duty,
                        backgroundColor = moduleButtonColor,
                        textColor = textColor
                    ) {
                        val namaLogin = displayName
                        navController.navigate(Screen.RekapPiket.createRoute(userId, namaLogin))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ModuleItem(
    title: String,
    iconRes: Int,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = textColor
            )
        }
    }
}
