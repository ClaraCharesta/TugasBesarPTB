package com.example.asramaku.laporan

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.asramaku.model.DummyData
import com.example.asramaku.model.Laporan
import com.example.asramaku.model.Notifikasi
import com.example.asramaku.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuatLaporan(navController: NavController) {
    var judulKerusakan by remember { mutableStateOf("") }
    var deskripsiKerusakan by remember { mutableStateOf("") }
    var lokasiKamar by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // Launcher galeri
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri = it }
    }

    // Launcher kamera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = tempCameraUri
        } else {
            Toast.makeText(context, "Gagal mengambil foto", Toast.LENGTH_SHORT).show()
        }
    }

    // Permission kamera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            tempCameraUri = createImageUri(context)
            tempCameraUri?.let { cameraLauncher.launch(it) }
        } else {
            Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Laporan Baru") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightYellow)
            )
        },
        containerColor = LightYellow
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Judul Kerusakan
            Text("Judul Kerusakan", fontSize = 14.sp, color = Color.Gray)
            OutlinedTextField(
                value = judulKerusakan,
                onValueChange = { judulKerusakan = it },
                placeholder = { Text("Masukkan judul kerusakan") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = DarkTeal,
                    unfocusedBorderColor = Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Deskripsi Kerusakan
            Text("Deskripsi Kerusakan", fontSize = 14.sp, color = Color.Gray)
            OutlinedTextField(
                value = deskripsiKerusakan,
                onValueChange = { deskripsiKerusakan = it },
                placeholder = { Text("Jelaskan kerusakan yang terjadi") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = DarkTeal,
                    unfocusedBorderColor = Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Lokasi/Kamar
            Text("Lokasi/Kamar", fontSize = 14.sp, color = Color.Gray)
            OutlinedTextField(
                value = lokasiKamar,
                onValueChange = { lokasiKamar = it },
                placeholder = { Text("Masukkan nomor kamar") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = DarkTeal,
                    unfocusedBorderColor = Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Upload Foto
            Text("Upload Foto", fontSize = 14.sp, color = Color.Gray)
            Button(
                onClick = { showImagePickerDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Pilih Foto", tint = DarkTeal)
                Spacer(Modifier.width(8.dp))
                Text("Tambahkan Foto", color = DarkTeal)
            }

            Spacer(Modifier.height(16.dp))

            // Preview Foto
            Text("Foto Barang Rusak", fontSize = 14.sp, color = Color.Gray)
            if (imageUri != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PhotoLibrary,
                            contentDescription = "Foto Barang Rusak",
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkTeal),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Kirim Laporan", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = RedButton),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Batal", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Image picker dialog
    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Pilih Foto") },
            text = { Text("Pilih sumber foto") },
            confirmButton = {
                TextButton(onClick = {
                    showImagePickerDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Galeri", color = DarkTeal)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImagePickerDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Kamera", color = DarkTeal)
                }
            },
            containerColor = Color.White
        )
    }

    // Confirmation dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Apakah kamu yakin?") },
            confirmButton = {
                TextButton(onClick = {
                    val newLaporan = Laporan(
                        id = (DummyData.daftarLaporan.size + 1).toString(),
                        judulKerusakan = judulKerusakan,
                        deskripsiKerusakan = deskripsiKerusakan,
                        lokasiKamar = lokasiKamar,
                        fotoUrl = imageUri?.toString(),
                        tanggal = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date()),
                        status = "Menunggu"
                    )
                    DummyData.daftarLaporan.add(0, newLaporan)

                    val newNotifikasi = Notifikasi(
                        id = (DummyData.daftarNotifikasi.size + 1).toString(),
                        judul = "Laporan berhasil dikirim",
                        pesan = "Laporan '$judulKerusakan' telah dikirim",
                        waktu = "Baru saja",
                        tipe = "kirim"
                    )
                    DummyData.daftarNotifikasi.add(0, newNotifikasi)

                    showDialog = false
                    navController.navigateUp()
                }) {
                    Text("Ya", color = DarkTeal)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Tidak", color = RedButton)
                }
            },
            containerColor = Color.White
        )
    }
}

// Helper function untuk membuat URI foto (sudah sesuai manifest)
private fun createImageUri(context: Context): Uri? {
    return try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!dir!!.exists()) dir.mkdirs()
        val file = File(dir, fileName)

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider", // WAJIB SAMA DENGAN MANIFEST
            file
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
