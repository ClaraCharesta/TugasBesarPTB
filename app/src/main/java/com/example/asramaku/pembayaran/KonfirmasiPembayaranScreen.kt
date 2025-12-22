package com.example.asramaku.pembayaran

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.asramaku.R
import com.example.asramaku.component.RekeningCard
import com.example.asramaku.navigation.Screen
import com.example.asramaku.screens.PaymentViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KonfirmasiPembayaranScreen(
    navController: NavController,
    viewModel: PaymentViewModel,
    userId: Int,
    bulan: String,
    total: Int
) {
    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showSuccessPopup by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val isLoading by viewModel.isLoading.collectAsState()
    val successMsg by viewModel.successMessage.collectAsState()
    val errorMsg by viewModel.errorMessage.collectAsState()

    // ===== IMAGE PICKER =====
    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) selectedImageUri = uri
    }

    val tempCameraUri = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) selectedImageUri = tempCameraUri.value
    }

    fun createImageUri(context: Context): Uri {
        val imageFile = File(
            context.cacheDir,
            "bukti_bayar_${System.currentTimeMillis()}.jpg"
        )
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    }

    LaunchedEffect(successMsg) {
        if (successMsg.isNotEmpty()) {
            showSuccessPopup = true
        }
    }


    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFFF0D5))
        ) {

            TopAppBar(
                title = { Text("Lakukan Pembayaran") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFAED6D3)
                )
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = bulan,
                    onValueChange = {},
                    label = { Text("Tagihan Bulan") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )

                OutlinedTextField(
                    value = total.toString(),
                    onValueChange = {},
                    label = { Text("Total Tagihan") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )

                Text("Metode Pembayaran", style = MaterialTheme.typography.titleMedium)

                RekeningCard("BCA", "70055792666", "Asrama", R.drawable.ic_bca)
                RekeningCard("BNI", "18005579266", "Asrama", R.drawable.ic_bni)

                Text("Upload Bukti Pembayaran", style = MaterialTheme.typography.titleMedium)

                OutlinedButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = successMsg.isEmpty()
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Pilih Foto / Kamera")
                }

                selectedImageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Bukti Pembayaran",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Button(
                        onClick = {
                            viewModel.submitPayment(
                                context = context,
                                userId = userId,
                                bulan = bulan,
                                totalTagihan = total,
                                buktiBayarUri = selectedImageUri
                            )
                        },
                        enabled = selectedImageUri != null &&
                                !isLoading &&
                                successMsg.isEmpty(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(Color(0xFF2E6664))
                    ) {
                        Text("Kirim", color = Color.White)
                    }

                    Button(
                        onClick = { navController.popBackStack() },
                        enabled = successMsg.isEmpty(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(Color(0xFF2E6664))
                    ) {
                        Text("Batal", color = Color.White)
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator()
                }

                if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = Color.Red)
                }
            }
        }
    }


    if (showDialog && successMsg.isEmpty()) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Pilih Metode Upload") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            pickImageLauncher.launch("image/*")
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📁 Pilih dari Galeri")
                    }

                    Button(
                        onClick = {
                            val uri = createImageUri(context)
                            tempCameraUri.value = uri
                            cameraLauncher.launch(uri)
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📷 Ambil Foto Kamera")
                    }
                }
            },
            confirmButton = {}
        )
    }


    if (showSuccessPopup) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Berhasil!") },
            text = { Text("Bukti pembayaran berhasil dikirim.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessPopup = false
                        navController.navigate(Screen.Payment.createRoute(userId)) {
                            popUpTo(Screen.Payment.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}