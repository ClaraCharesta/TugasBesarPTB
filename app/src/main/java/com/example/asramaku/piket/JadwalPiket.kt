package com.example.asramaku.piket

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asramaku.data.local.TokenManager
import com.example.asramaku.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// =======================
// DATA CLASS
// =======================
data class PiketResponse(
    val id: Int,
    val userId: Int,
    val tanggal: String,
    val status: String
)

// =======================
// SCREEN
// =======================
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalPiketScreen(
    navController: NavController,
    userId: Int,
    namaLogin: String
) {
    // ----- ambil session (fallback) tanpa merubah UI -----
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val savedUserId by tokenManager.userId.collectAsState(initial = 0)
    val savedUserName by tokenManager.userName.collectAsState(initial = "")

    // effective values: gunakan argumen kalau ada, kalau tidak pakai DataStore
    val effectiveUserId = remember(userId, savedUserId) { if (userId != 0) userId else savedUserId }
    val effectiveNamaLogin = remember(namaLogin, savedUserName) { if (namaLogin.isNotBlank()) namaLogin else savedUserName }

    val backgroundColor = Color(0xFFFFE7C2)
    val cardColor = Color(0xFFB6D9D1)
    val textFieldBg = Color(0xFFE6E1DC)
    val buttonSelesai = Color(0xFF325B5C)
    val buttonGanti = Color(0xFFFF3B30)

    var searchQuery by remember { mutableStateOf("") }
    var piketList by remember { mutableStateOf<List<PiketResponse>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch data dari server sesuai effectiveUserId
    LaunchedEffect(effectiveUserId) {
        if (effectiveUserId != 0) {
            coroutineScope.launch {
                piketList = getPiketFromServer(effectiveUserId)
            }
        } else {
            // kalau belum ada userId (misal belum login), kosongi list
            piketList = emptyList()
        }
    }

    val currentDate = LocalDate.now()
    val currentTime = LocalTime.now()
    val filteredList = piketList.filter { it.tanggal.contains(searchQuery) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Jadwal Piket Saya",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF325B5C)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF325B5C)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(textFieldBg),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text("Search (yyyy-MM-dd)", color = Color.Gray, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List piket
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tidak ada jadwal piket", color = Color.Gray)
                    }
                } else {
                    filteredList.forEach { piket ->
                        val tanggalPiket = try {
                            LocalDate.parse(piket.tanggal)
                        } catch (e: Exception) {
                            LocalDate.now()
                        }
                        val formatter = DateTimeFormatter.ofPattern("dd - MM - yyyy")

                        val status = when {
                            currentDate.isBefore(tanggalPiket) -> "Belum Dikerjakan"
                            currentDate.isEqual(tanggalPiket) && currentTime.isBefore(LocalTime.of(17, 0)) -> "Belum Dikerjakan"
                            else -> "Ganti Piket"
                        }

                        val buttonColor by animateColorAsState(
                            if (status == "Ganti Piket") buttonGanti else buttonSelesai
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .animateContentSize(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    // TAMPILAN UI TETAP SAMA: gunakan effectiveNamaLogin
                                    Text(
                                        text = "Nama : $effectiveNamaLogin",
                                        fontSize = 16.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Tanggal : ${tanggalPiket.format(formatter)}",
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(buttonColor)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .clickable {
                                            val encodedTanggal = Uri.encode(tanggalPiket.toString())
                                            if (status == "Belum Dikerjakan") {
                                                // tetap gunakan createRoute sesuai project-mu (tidak diubah)
                                                navController.navigate(
                                                    Screen.BelumDikerjakan.createRoute(
                                                        nama = effectiveNamaLogin,
                                                        tanggal = encodedTanggal
                                                    )
                                                )
                                            } else {
                                                navController.navigate(
                                                    Screen.GantiPiket.createRoute(
                                                        nama = effectiveNamaLogin,
                                                        tanggal = encodedTanggal
                                                    )
                                                )
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = status, color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =====================
// API FUNCTION
// =====================
@SuppressLint("NewApi")
suspend fun getPiketFromServer(userId: Int): List<PiketResponse> {
    return withContext(Dispatchers.IO) {
        try {
            // 1. Bikin koneksi
            val url = URL("http://10.0.2.2:3000/api/piket/$userId")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            // 2. Baca response
            val result = conn.inputStream.bufferedReader().readText()
            println("Hasil fetch dari server: $result") // <- cek di Logcat

            val piketList = mutableListOf<PiketResponse>()

            // 3. Cek apakah JSON berbentuk array atau object
            if (result.trim().startsWith("[")) {
                // JSON langsung array
                val jsonArr = JSONArray(result)
                for (i in 0 until jsonArr.length()) {
                    val obj = jsonArr.getJSONObject(i)
                    val tanggalValue = obj.optString("tanggal", LocalDate.now().toString())
                    piketList.add(
                        PiketResponse(
                            id = obj.optInt("id", 0),
                            userId = obj.optInt("userId", 0),
                            tanggal = tanggalValue,
                            status = obj.optString("status", "Belum Dikerjakan")
                        )
                    )
                }
            } else if (result.trim().startsWith("{")) {
                // JSON object, kemungkinan ada field "data"
                val jsonObj = org.json.JSONObject(result)
                val jsonArr = jsonObj.optJSONArray("data") ?: JSONArray()
                for (i in 0 until jsonArr.length()) {
                    val obj = jsonArr.getJSONObject(i)
                    val tanggalValue = obj.optString("tanggal", LocalDate.now().toString())
                    piketList.add(
                        PiketResponse(
                            id = obj.optInt("id", 0),
                            userId = obj.optInt("userId", 0),
                            tanggal = tanggalValue,
                            status = obj.optString("status", "Belum Dikerjakan")
                        )
                    )
                }
            } else {
                println("Response tidak dikenali: $result")
            }

            // 4. Kembalikan list
            piketList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
