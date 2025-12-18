package com.example.asramaku.piket.rekap

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.asramaku.piket.RiwayatPiket
import com.example.asramaku.piket.ApiClient
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPiketSayaScreen(
    navController: NavController,
    tanggal: String, // diambil dari RekapPiket
    userId: Int = 0 // default 0, nanti diambil dari TokenManager
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val savedUserId by tokenManager.userId.collectAsState(initial = 0)
    val savedUserName by tokenManager.userName.collectAsState(initial = "User")
    val effectiveUserId = if (userId != 0) userId else savedUserId

    val backgroundColor = Color(0xFFFFE7C2)
    val cardColor = Color(0xFF9DBEBB)
    val selesaiColor = Color(0xFF3BB54A)

    var piketDetail by remember { mutableStateOf<RiwayatPiket?>(null) }
    val scope = rememberCoroutineScope()
    val formatterDisplay = DateTimeFormatter.ofPattern("dd - MM - yyyy")

    // ===================== FETCH DATA =====================
    LaunchedEffect(effectiveUserId, tanggal) {
        scope.launch {
            try {
                val allPiket = ApiClient.riwayatApi.getRiwayatPiket(effectiveUserId)
                piketDetail = allPiket.find { it.tanggal == tanggal && it.status == "Selesai" }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Piket Saya", fontSize = 20.sp, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (piketDetail == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Memuat detail piket...", color = Color.Gray)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardColor)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ===================== HEADER =====================
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Nama : $savedUserName", // gunakan nama asli user
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Tanggal : ${LocalDate.parse(piketDetail!!.tanggal).format(formatterDisplay)}",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(selesaiColor)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Selesai",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ===================== FOTO =====================
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFD8CFC4)),
                        contentAlignment = Alignment.Center
                    ) {
                        val fotoUri = piketDetail!!.foto ?: "https://via.placeholder.com/150"
                        Image(
                            painter = rememberAsyncImagePainter(fotoUri),
                            contentDescription = "Foto Piket",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
