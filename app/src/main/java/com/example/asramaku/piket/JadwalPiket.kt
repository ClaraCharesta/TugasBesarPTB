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
import java.time.format.DateTimeFormatter


data class PiketResponse(
    val id: Int,
    val userId: Int,
    val tanggal: String,
    val status: String
)



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalPiketScreen(
    navController: NavController,
    userId: Int,
    namaLogin: String
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val savedUserId by tokenManager.userId.collectAsState(initial = 0)
    val savedUserName by tokenManager.userName.collectAsState(initial = "")

    val effectiveUserId = if (userId != 0) userId else savedUserId
    val effectiveNamaLogin = if (namaLogin.isNotBlank()) namaLogin else savedUserName

    val backgroundColor = Color(0xFFFFE7C2)
    val cardColor = Color(0xFFB6D9D1)
    val textFieldBg = Color(0xFFE6E1DC)
    val buttonBelum = Color(0xFF325B5C)
    val buttonTelat = Color(0xFFFF3B30)
    val buttonSelesai = Color.Gray

    var searchQuery by remember { mutableStateOf("") }
    var piketList by remember { mutableStateOf<List<PiketResponse>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(effectiveUserId) {
        if (effectiveUserId != 0) {
            coroutineScope.launch {
                piketList = getPiketFromServer(effectiveUserId)
            }
        }
    }


    val filteredList = piketList.filter {
        (it.status.equals("Belum Dikerjakan", ignoreCase = true) ||
                it.status.equals("Telat Dikerjakan", ignoreCase = true)) &&
                it.tanggal.contains(searchQuery)
    }

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

            // ====================== SEARCH BAR =======================
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

                        val status = piket.status

                        val buttonColor by animateColorAsState(
                            when (status) {
                                "Belum Dikerjakan" -> buttonBelum
                                "Telat Dikerjakan" -> buttonTelat
                                "Selesai" -> buttonSelesai
                                else -> buttonBelum
                            }
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

                                            when (status) {
                                                "Belum Dikerjakan" -> {
                                                    navController.navigate(
                                                        Screen.BelumDikerjakan.createRoute(
                                                            userId = effectiveUserId,
                                                            jadwalId = piket.id, // ⬅️ INI PENTING
                                                            nama = effectiveNamaLogin,
                                                            tanggal = tanggalPiket.toString() // JANGAN encode
                                                        )
                                                    )
                                                }



                                                "Telat Dikerjakan" -> {
                                                    navController.navigate(
                                                        Screen.GantiPiket.createRoute(
                                                            nama = effectiveNamaLogin,
                                                            piketId = piket.id,       // kirim piketId agar API tahu data mana yang diupdate
                                                            tanggal = encodedTanggal   // tanggal lama, ditampilkan di kalender
                                                        )
                                                    )
                                                }


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



@SuppressLint("NewApi")
suspend fun getPiketFromServer(userId: Int): List<PiketResponse> {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("http://10.0.2.2:3000/api/piket/$userId")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            val result = conn.inputStream.bufferedReader().readText()
            println("Hasil fetch dari server: $result")

            val piketList = mutableListOf<PiketResponse>()

            if (result.trim().startsWith("[")) {
                val jsonArr = JSONArray(result)
                for (i in 0 until jsonArr.length()) {
                    val obj = jsonArr.getJSONObject(i)
                    piketList.add(
                        PiketResponse(
                            id = obj.optInt("id", 0),
                            userId = obj.optInt("userId", 0),
                            tanggal = obj.optString("tanggal"),
                            status = obj.optString("status", "Belum Dikerjakan")
                        )
                    )
                }
            }

            piketList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
