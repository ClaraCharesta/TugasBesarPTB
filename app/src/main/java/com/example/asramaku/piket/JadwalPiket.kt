package com.example.asramaku.piket

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.example.asramaku.navigation.Screen

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalPiketScreen(
    navController: NavController,
    namaLogin: String = "Clara"
) {
    val backgroundColor = Color(0xFFFFE7C2)
    val cardColor = Color(0xFFB6D9D1)
    val textFieldBg = Color(0xFFE6E1DC)
    val buttonSelesai = Color(0xFF325B5C)
    val buttonGanti = Color(0xFFFF3B30)

    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Daftar tanggal jadwal
    val jadwalList = listOf(
        "2025-10-28",
        "2025-10-30",
        "2025-10-31",
        "2026-11-02",
        "2026-11-05"
    )

    val currentDate = LocalDate.now()
    val currentTime = LocalTime.now()

    val filteredList = jadwalList.filter { it.contains(searchQuery) }

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
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
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
                            Text(
                                text = "Search (yyyy-MM-dd)",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daftar Jadwal
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredList.forEach { tanggalStr ->
                    val tanggalPiket = LocalDate.parse(tanggalStr)
                    val formatterDisplay = DateTimeFormatter.ofPattern("dd - MM - yyyy")

                    // Tentukan status
                    val status = when {
                        currentDate.isBefore(tanggalPiket) -> "Belum Dikerjakan"
                        currentDate.isEqual(tanggalPiket) && currentTime.isBefore(LocalTime.of(17, 0)) -> "Belum Dikerjakan"
                        else -> "Ganti Piket"
                    }

                    val buttonColor by animateColorAsState(
                        targetValue = if (status == "Ganti Piket") buttonGanti else buttonSelesai
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
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Nama : $namaLogin",
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Tanggal : ${tanggalPiket.format(formatterDisplay)}",
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }

                            // Tombol dinamis
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(buttonColor)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .clickable {
                                        if (status == "Belum Dikerjakan") {
                                            navController.navigate(
                                                Screen.BelumDikerjakan.createRoute(
                                                    nama = namaLogin,
                                                    tanggal = tanggalPiket.toString()
                                                )
                                            )
                                        } else if (status == "Ganti Piket") {
                                            navController.navigate(
                                                Screen.GantiPiket.createRoute(
                                                    nama = namaLogin,
                                                    tanggal = tanggalPiket.toString()
                                                )
                                            )
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = status,
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
