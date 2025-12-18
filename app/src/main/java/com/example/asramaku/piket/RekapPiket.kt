package com.example.asramaku.piket

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.asramaku.data.local.TokenManager
import com.example.asramaku.navigation.Screen
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// ===================== DATA MODEL =====================
data class RiwayatPiket(
    val id: Int,
    val userId: Int,
    val tanggal: String,
    val foto: String? = null,
    val status: String
)

// ===================== RETROFIT API =====================
interface RiwayatApi {
    @GET("api/piket/riwayat/{userId}")
    suspend fun getRiwayatPiket(@Path("userId") userId: Int): List<RiwayatPiket>
}

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"
    val riwayatApi: RiwayatApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RiwayatApi::class.java)
    }
}

// ===================== KOMPOSABLE =====================
@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekapPiketSayaScreen(
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
    val cardColor = Color(0xFF9DBEBB)
    val selesaiColor = Color(0xFF3BB54A)
    val detailButtonColor = Color(0xFF325B5C)
    val textFieldBg = Color(0xFFE6E1DC)

    var searchQuery by remember { mutableStateOf("") }
    var jadwalList by remember { mutableStateOf(listOf<RiwayatPiket>()) }
    val scope = rememberCoroutineScope()
    val formatterDisplay = DateTimeFormatter.ofPattern("dd - MM - yyyy")

    // ===================== FETCH DATA =====================
    LaunchedEffect(effectiveUserId) {
        scope.launch {
            try {
                jadwalList = ApiClient.riwayatApi.getRiwayatPiket(effectiveUserId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ===================== FILTER SEARCH =====================
    val filteredList = jadwalList.filter { it.tanggal.contains(searchQuery) }

    // ===================== UI =====================
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Rekap Piket Saya",
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
                .fillMaxSize()
                .padding(horizontal = 16.dp)
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
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
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

            // ====================== LIST CARD =======================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada data piket",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    filteredList.forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                // ===================== NAMA & TANGGAL & FOTO =====================
                                Column {
                                    Text(
                                        text = "Nama : $effectiveNamaLogin",
                                        fontSize = 16.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Tanggal : ${LocalDate.parse(item.tanggal).format(formatterDisplay)}",
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            item.foto ?: "https://via.placeholder.com/150"
                                        ),
                                        contentDescription = "Foto Piket",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(60.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // ===================== STATUS =====================
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(selesaiColor)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Selesai",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // ===================== BUTTON LIHAT DETAIL =====================
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(detailButtonColor)
                                        .clickable {
                                            navController.navigate(
                                                Screen.DetailPiketSaya.createRoute(
                                                    tanggal = item.tanggal,
                                                    userId = effectiveUserId
                                                )
                                            )



                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Lihat Detail",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
