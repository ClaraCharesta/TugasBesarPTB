package com.example.asramaku.pembayaran

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.asramaku.component.RekeningCard
import com.example.asramaku.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KonfirmasiPembayaranScreen(
    // 🔹 Parameter disamakan dengan yang dipanggil dari MainActivity
    bulan: String = "",
    nama: String = "",
    noKamar: String = "",
    totalTagihan: String = "",
    onBackClick: () -> Unit = {},
    onSubmitClick: (String, String, String, String, Uri?) -> Unit = { _, _, _, _, _ -> },
    onCancelClick: () -> Unit = {},
    navigateToRiwayat: () -> Unit = {}
) {
    // 🔹 Nilai default otomatis terisi dari navController
    var inputNama by remember { mutableStateOf(nama) }
    var inputBulan by remember { mutableStateOf(bulan) }
    var inputNoKamar by remember { mutableStateOf(noKamar) }
    var inputTotalTagihan by remember { mutableStateOf(totalTagihan) }

    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val tempCameraUri = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success -> if (success) selectedImageUri = tempCameraUri.value }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { selectedImageUri = it } }

    fun createImageUri(context: Context): Uri {
        val imageFile = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0D5))
    ) {
        TopAppBar(
            title = { Text("Lakukan Pembayaran") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFAED6D3))
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = inputNama,
                onValueChange = { inputNama = it },
                label = { Text("Nama") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = inputBulan,
                onValueChange = { inputBulan = it },
                label = { Text("Tagihan Bulan") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = inputNoKamar,
                onValueChange = { inputNoKamar = it },
                label = { Text("No. Kamar") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = inputTotalTagihan,
                onValueChange = { inputTotalTagihan = it },
                label = { Text("Total Tagihan") },
                keyboardOptions = KeyboardOptions.Default,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Metode Pembayaran", style = MaterialTheme.typography.titleMedium)
            RekeningCard(bank = "BCA", nomor = "70055792666", nama = "Asrama", logo = R.drawable.ic_bca)
            RekeningCard(bank = "BNI", nomor = "18005579266", nama = "Asrama", logo = R.drawable.ic_bni)

            Text("Upload Bukti Pembayaran", style = MaterialTheme.typography.titleMedium)
            OutlinedButton(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pilih Foto / Kamera")
            }

            selectedImageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Bukti Pembayaran",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        if (inputNama.isNotBlank() && inputBulan.isNotBlank() &&
                            inputNoKamar.isNotBlank() && inputTotalTagihan.isNotBlank() &&
                            selectedImageUri != null
                        ) {
                            onSubmitClick(
                                inputNama,
                                inputBulan,
                                inputNoKamar,
                                inputTotalTagihan,
                                selectedImageUri
                            )
                            navigateToRiwayat()
                        } else {
                            println("⚠️ Harap isi semua data terlebih dahulu!")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E6664))
                ) { Text("Kirim") }

                Button(
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E6664))
                ) { Text("Batal") }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(shape = MaterialTheme.shapes.medium, color = Color.White) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Pilih Metode Upload", style = MaterialTheme.typography.titleMedium)
                    Button(
                        onClick = {
                            showDialog = false
                            pickImageLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("📁 Pilih Foto") }

                    Button(
                        onClick = {
                            showDialog = false
                            val newUri = createImageUri(context)
                            tempCameraUri.value = newUri
                            cameraLauncher.launch(newUri)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("📸 Ambil Foto") }
                }
            }
        }
    }
}
