package com.example.asramaku.laporan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asramaku.data.local.TokenManager
import com.example.asramaku.data.remote.RetrofitClient
import com.example.asramaku.model.Laporan
import com.example.asramaku.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarLaporan(navController: NavController) {

    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    var laporanList by remember { mutableStateOf<List<Laporan>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var selectedReportId by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val token = tokenManager.token.first()
            val response = RetrofitClient.instance.getMyReports(
                token = "Bearer $token"
            )

            if (response.isSuccessful) {
                laporanList = response.body()?.data ?: emptyList()
            } else {
                errorMessage = "Gagal memuat laporan"
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
                title = { Text("Daftar Laporan Saya") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightYellow)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = LightYellow
    ) { paddingValues ->

        if (showDeleteDialog && selectedReportId != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Hapus laporan?", fontWeight = FontWeight.Bold) },
                text = { Text("Laporan yang dihapus tidak bisa dikembalikan.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            scope.launch {
                                try {
                                    val token = tokenManager.token.first()
                                    RetrofitClient.instance.deleteReport(
                                        token = "Bearer $token",
                                        id = selectedReportId!!
                                    )

                                    laporanList = laporanList.filter {
                                        it.id != selectedReportId
                                    }

                                    snackbarHostState.showSnackbar(
                                        "Laporan berhasil dihapus"
                                    )
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar(
                                        "Gagal menghapus laporan"
                                    )
                                }
                            }
                        }
                    ) {
                        Text("Hapus", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(errorMessage ?: "Terjadi kesalahan")
                }
            }

            laporanList.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada laporan", color = Color.Gray)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(laporanList) { laporan ->
                        LaporanCard(
                            laporan = laporan,
                            onDetailClick = {
                                navController.navigate("detail_laporan/${laporan.id}")
                            },
                            onEditClick = {
                                navController.navigate("edit_laporan/${laporan.id}")
                            },
                            onDeleteClick = {
                                selectedReportId = laporan.id
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LaporanCard(
    laporan: Laporan,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightTeal)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = laporan.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkTeal,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (laporan.status.lowercase()) {
                        "pending" -> Color(0xFFFFE5E5)
                        "process" -> Color(0xFFFFF3E0)
                        else -> Color(0xFFE8F5E9)
                    }
                ) {
                    Text(
                        text = laporan.status,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = laporan.createdAt,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onDetailClick,
                    modifier = Modifier.weight(1f)
                ) { Text("Lihat") }

                Button(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = LightYellow)
                ) { Text("Edit", color = DarkTeal) }

                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = RedButton)
                ) { Text("Hapus") }
            }
        }
    }
}