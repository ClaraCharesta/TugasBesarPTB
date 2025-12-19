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
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.asramaku.data.local.TokenManager
import com.example.asramaku.data.remote.RetrofitClient
import com.example.asramaku.ui.theme.DarkTeal
import com.example.asramaku.ui.theme.LightYellow
import com.example.asramaku.ui.theme.RedButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuatLaporan(navController: NavController) {

    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    // ====== AMBIL TOKEN DARI DATASTORE ======
    val tokenManager = TokenManager(context)
    var token by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        tokenManager.token.collect {
            token = it
        }
    }

    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    var showPicker by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { imageUri = it } }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) imageUri = tempCameraUri
    }

    val cameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            tempCameraUri = createImageUri(context)
            tempCameraUri?.let { cameraLauncher.launch(it) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Laporan Baru") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightYellow)
            )
        },
        containerColor = LightYellow
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // JUDUL
            Text("Judul Kerusakan", color = Color.Gray)
            OutlinedTextField(
                value = judul,
                onValueChange = { judul = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.height(16.dp))

            // DESKRIPSI
            Text("Deskripsi", color = Color.Gray)
            OutlinedTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.height(16.dp))

            // LOKASI
            Text("Lokasi/Kamar", color = Color.Gray)
            OutlinedTextField(
                value = lokasi,
                onValueChange = { lokasi = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.height(16.dp))

            // BUTTON PILIH FOTO
            Text("Foto Barang Rusak", color = Color.Gray)
            Button(
                onClick = { showPicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = DarkTeal)
                Spacer(Modifier.width(8.dp))
                Text("Pilih Foto", color = DarkTeal)
            }

            Spacer(Modifier.height(16.dp))

            // PREVIEW GAMBAR
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(24.dp))

            // KIRIM
            Button(
                onClick = { showConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkTeal)
            ) {
                Text("Kirim Laporan", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Dialog pilih sumber foto
    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text("Pilih Foto") },
            text = { Text("Pilih sumber foto") },
            confirmButton = {
                TextButton(onClick = {
                    showPicker = false
                    galleryLauncher.launch("image/*")
                }) { Text("Galeri", color = DarkTeal) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPicker = false
                    cameraPermission.launch(Manifest.permission.CAMERA)
                }) { Text("Kamera", color = DarkTeal) }
            }
        )
    }

    // Dialog konfirmasi upload
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Kirim laporan ini?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false

                    coroutine.launch {
                        if (token.isBlank()) {
                            Toast.makeText(context, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        val ok = uploadLaporan(
                            context,
                            judul,
                            deskripsi,
                            lokasi,
                            imageUri,
                            token
                        )

                        if (ok) {
                            Toast.makeText(context, "Laporan terkirim", Toast.LENGTH_SHORT).show()
                            navController.navigateUp()
                        } else {
                            Toast.makeText(context, "Gagal mengirim laporan", Toast.LENGTH_SHORT).show()
                        }
                    }

                }) { Text("Ya", color = DarkTeal) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("Tidak", color = RedButton)
                }
            }
        )
    }
}

// ================= UPLOAD FUNCTION =================

// ================= UPLOAD FUNCTION FINAL =================
suspend fun uploadLaporan(
    context: Context,
    title: String,
    description: String,
    location: String,
    imageUri: Uri?,
    token: String
): Boolean {
    return try {
        if (title.isBlank() || description.isBlank()) {
            Toast.makeText(context, "Judul dan deskripsi wajib diisi", Toast.LENGTH_SHORT).show()
            return false
        }

        if (token.isBlank()) {
            Toast.makeText(context, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
            return false
        }

        val api = RetrofitClient.instance

        // ===== RequestBody untuk teks =====
        val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val descBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val locationBody = location.toRequestBody("text/plain".toMediaTypeOrNull())

        // ===== Multipart file (opsional) =====
        val photoPart: MultipartBody.Part? = imageUri?.let { uri ->
            val file = File(getPathFromUri(context, uri))
            if (!file.exists() || file.length() == 0L) {
                Toast.makeText(context, "File foto tidak valid", Toast.LENGTH_SHORT).show()
                return false
            }
            val request = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photo", file.name, request)
        }

        // ===== Kirim ke API =====
        val response = api.createReport(
            token = "Bearer $token",
            title = titleBody,
            description = descBody,
            location = locationBody,
            photo = photoPart
        )

        if (response.isSuccessful) {
            true
        } else {
            // Bisa cek error dari backend
            val msg = response.errorBody()?.string()
            Toast.makeText(context, "Gagal: $msg", Toast.LENGTH_SHORT).show()
            false
        }

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
        false
    }
}

// ===== Fungsi bantu create URI kamera =====
fun createImageUri(context: Context): Uri? {
    val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IMG_$time.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

// ===== Fungsi bantu ambil path dari URI =====
fun getPathFromUri(context: Context, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Tidak bisa membuka URI")
    val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
    file.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return file.absolutePath
}
