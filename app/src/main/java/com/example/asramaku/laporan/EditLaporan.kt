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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.example.asramaku.model.Notifikasi
import com.example.asramaku.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLaporan(navController: NavController, laporanId: String) {
    val laporanIndex = DummyData.daftarLaporan.indexOfFirst { it.id == laporanId }

    if (laporanIndex == -1) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Laporan tidak ditemukan")
        }
        return
    }

    val laporan = DummyData.daftarLaporan[laporanIndex]

    var judulKerusakan by remember { mutableStateOf(laporan.judulKerusakan) }
    var deskripsiKerusakan by remember { mutableStateOf(laporan.deskripsiKerusakan) }
    var lokasiKamar by remember { mutableStateOf(laporan.lokasiKamar) }
    var imageUri by remember { mutableStateOf<Uri?>(laporan.fotoUrl?.let { Uri.parse(it) }) }
    var showDialog by remember { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = tempCameraUri
        }
    }

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
                title = { Text("Edit Laporan") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightYellow
                )
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
            // Nama Barang
            Text(
                text = "Nama Barang",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = judulKerusakan,
                onValueChange = { judulKerusakan = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = DarkTeal,
                    unfocusedBorderColor = Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Deskripsi Kerusakan
            Text(
                text = "Deskripsi Kerusakan",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = deskripsiKerusakan,
                onValueChange = { deskripsiKerusakan = it },
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

            Spacer(modifier = Modifier.height(16.dp))

            // Lokasi/Kamar
            Text(
                text = "Lokasi/Kamar",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = lokasiKamar,
                onValueChange = { lokasiKamar = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = DarkTeal,
                    unfocusedBorderColor = Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Upload Foto
            Text(
                text = "Upload Foto",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Button(
                onClick = { showImagePickerDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Default.PhotoLibrary,
                    contentDescription = "Pilih Foto",
                    tint = DarkTeal
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambahkan Foto", color = DarkTeal)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preview Foto
            Text(
                text = "Foto Barang Rusak",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

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
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
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

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkTeal
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Kirim Perubahan", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Button(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedButton
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Batal", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Image Picker Dialog
    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Pilih Foto") },
            text = { Text("Pilih sumber foto") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImagePickerDialog = false
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text("Galeri", color = DarkTeal)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImagePickerDialog = false
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("Kamera", color = DarkTeal)
                }
            },
            containerColor = Color.White
        )
    }

    // Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Apakah kamu yakin?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Update laporan
                        DummyData.daftarLaporan[laporanIndex] = laporan.copy(
                            judulKerusakan = judulKerusakan,
                            deskripsiKerusakan = deskripsiKerusakan,
                            lokasiKamar = lokasiKamar,
                            fotoUrl = imageUri?.toString()
                        )

                        // Tambah notifikasi
                        val newNotifikasi = Notifikasi(
                            id = (DummyData.daftarNotifikasi.size + 1).toString(),
                            judul = "Laporan diubah",
                            pesan = "Laporan '$judulKerusakan' telah diubah",
                            waktu = "Baru saja",
                            tipe = "edit"
                        )
                        DummyData.daftarNotifikasi.add(0, newNotifikasi)

                        showDialog = false
                        navController.navigateUp()
                    }
                ) {
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

// Helper function untuk membuat URI untuk kamera
private fun createImageUri(context: Context): Uri? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    return try {
        val imageFile = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}