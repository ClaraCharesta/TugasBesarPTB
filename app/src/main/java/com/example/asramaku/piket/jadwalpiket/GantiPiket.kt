package com.example.asramaku.piket.jadwalpiket

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asramaku.data.local.TokenManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray


suspend fun updateTanggalPiket(slotId: Int, userId: Int, newTanggal: String): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("http://10.0.2.2:3000/api/piket/update-tanggal")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "PUT"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val body = """{"piketId":$slotId,"newTanggal":"$newTanggal"}"""
            conn.outputStream.use { it.write(body.toByteArray()) }

            conn.responseCode == 200
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}


@SuppressLint("NewApi")
suspend fun getTanggalPiketTerpakai(): Set<LocalDate> {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("http://10.0.2.2:3000/api/piket/tanggal-terpakai")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"

            val result = conn.inputStream.bufferedReader().readText()
            val jsonArr = JSONArray(result)

            val dates = mutableSetOf<LocalDate>()
            for (i in 0 until jsonArr.length()) {
                dates.add(LocalDate.parse(jsonArr.getString(i)))
            }
            dates
        } catch (e: Exception) {
            emptySet()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GantiPiketCalendarScreen(
    navController: NavController,
    slotId: Int,
    tanggalLama: LocalDate
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val savedUserId by tokenManager.userId.collectAsState(initial = 0)

    val backgroundColor = Color(0xFFFFE7C2)
    val cardColor = Color(0xFF9DBEBB)
    val buttonColor = Color(0xFF325B5C)
    val selectedColor = Color(0xFFFC9E4F)
    val todayColor = Color(0xFF9DBEBB)
    val formatter = DateTimeFormatter.ofPattern("dd - MM - yyyy")

    var selectedDate by remember { mutableStateOf(tanggalLama) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentMonth = remember { mutableStateOf(YearMonth.now()) }


    var tanggalTerpakai by remember { mutableStateOf<Set<LocalDate>>(emptySet()) }

    LaunchedEffect(Unit) {
        tanggalTerpakai = getTanggalPiketTerpakai()
    }

    fun generateCalendarDates(month: YearMonth): List<LocalDate> {
        val firstDay = month.atDay(1)
        val totalDays = firstDay.dayOfWeek.value % 7
        val days = mutableListOf<LocalDate>()

        repeat(totalDays) { days.add(LocalDate.MIN) }
        for (day in 1..month.lengthOfMonth()) {
            days.add(month.atDay(day))
        }
        return days
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Tanggal Baru", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = backgroundColor
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Tanggal piket kadaluarsa: ${tanggalLama.format(formatter)}",
                fontSize = 16.sp
            )
            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = {
                    currentMonth.value = currentMonth.value.minusMonths(1)
                }) { Text("<") }

                Text(
                    currentMonth.value.month.name + " " + currentMonth.value.year,
                    fontSize = 18.sp
                )

                TextButton(onClick = {
                    currentMonth.value = currentMonth.value.plusMonths(1)
                }) { Text(">") }
            }

            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("M", "S", "S", "R", "K", "J", "S").forEach { day ->
                    Text(day, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            val dates = generateCalendarDates(currentMonth.value)

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(300.dp)
            ) {
                items(dates) { date ->

                    // ====================== LOGIKA DISABLE (TAMBAHAN) ======================
                    val isDisabled =
                        date == LocalDate.MIN ||
                                !date.isAfter(LocalDate.now()) ||
                                tanggalTerpakai.contains(date)

                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(40.dp)
                            .background(
                                color = when {
                                    date == LocalDate.MIN -> Color.Transparent
                                    isDisabled -> Color.LightGray
                                    date == selectedDate -> selectedColor
                                    date == LocalDate.now() -> todayColor
                                    else -> cardColor
                                },
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable(enabled = !isDisabled) {
                                selectedDate = date
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != LocalDate.MIN) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = if (isDisabled) Color.DarkGray else Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        val ok = updateTanggalPiket(
                            slotId,
                            savedUserId,
                            selectedDate.toString()
                        )
                        snackbarHostState.showSnackbar(
                            if (ok) "Tanggal piket berhasil diperbarui"
                            else "Gagal memperbarui tanggal"
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Tanggal Baru", color = Color.White)
            }
        }
    }
}
