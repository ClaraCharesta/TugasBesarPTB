package com.example.asramaku.laporan

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.asramaku.data.local.TokenManager
import com.example.asramaku.data.remote.RetrofitClient
import com.example.asramaku.model.Laporan
import com.example.asramaku.ui.theme.*
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailLaporan(
    navController: NavController,
    laporanId: Int
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var laporan by remember { mutableStateOf<Laporan?>(null) }

    LaunchedEffect(laporanId) {
        try {
            val token = tokenManager.token.first()

            if (token.isNotEmpty()) {
                val response = RetrofitClient.instance.getReportById(
                    token = "Bearer $token",
                    id = laporanId
                )

                if (response.isSuccessful) {
                    laporan = response.body()?.data
                    Log.d("DETAIL", "DATA LAPORAN: ${response.body()?.data}")
                } else {
                    errorMessage = "Gagal memuat detail laporan"
                }
            } else {
                errorMessage = "Token tidak ditemukan"
            }
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Laporan") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightYellow)
            )
        },
        containerColor = LightYellow
    ) { padding ->

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(errorMessage ?: "Terjadi kesalahan")
                }
            }

            laporan != null -> {
                val data = laporan!!

                // ================= URL FOTO FINAL =================
                val imageUrl = data.photoUrl?.takeIf { it.isNotBlank() }?.let {
                    if (it.startsWith("http")) {
                        it
                    } else {
                        "http://10.0.2.2:3000$it"
                    }
                }


                Log.d("DETAIL", "IMAGE URL: $imageUrl")

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = data.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkTeal
                    )

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = when (data.status) {
                            "pending" -> Color(0xFFFFE082)
                            "processing" -> Color(0xFF90CAF9)
                            else -> Color(0xFFA5D6A7)
                        }
                    ) {
                        Text(
                            text = data.status,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    // ================= FOTO =================
                    if (imageUrl != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            colors = CardDefaults.cardColors(containerColor = LightTeal),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text("Tidak ada foto", color = DarkTeal)
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Deskripsi Kerusakan", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Text(data.description)
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Lokasi", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Text(data.location ?: "-")
                        }
                    }
                }
            }
        }
    }
}