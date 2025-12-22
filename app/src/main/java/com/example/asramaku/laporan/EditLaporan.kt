package com.example.asramaku.laporan

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.asramaku.data.local.TokenManager
import com.example.asramaku.data.remote.RetrofitClient
import com.example.asramaku.ui.theme.DarkTeal
import com.example.asramaku.ui.theme.LightYellow
import com.example.asramaku.utils.createImageUri
import com.example.asramaku.utils.uriToFile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLaporan(
    navController: NavController,
    laporanId: Int
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var oldPhotoUrl by remember { mutableStateOf<String?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(laporanId) {
        try {
            val token = tokenManager.token.first()

            val response = RetrofitClient.instance.getReportById(
                token = "Bearer $token",
                id = laporanId
            )

            if (response.isSuccessful) {
                val data = response.body()?.data
                if (data != null) {
                    title = data.title ?: ""
                    description = data.description ?: ""
                    location = data.location ?: ""
                    oldPhotoUrl = data.photoUrl // ⬅️ SIMPAN FOTO LAMA
                }
            } else {
                Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) imageUri = tempCameraUri
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
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
                title = { Text("Edit Laporan") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightYellow
                )
            )
        },
        containerColor = LightYellow
    ) { padding ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nama Barang") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi Kerusakan") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Lokasi / Kamar") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { showImagePickerDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PhotoLibrary, null)
                Spacer(Modifier.width(8.dp))
                Text("Pilih Foto")
            }

            Spacer(Modifier.height(12.dp))

            when {
                imageUri != null -> {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                oldPhotoUrl != null -> {
                    Image(
                        painter = rememberAsyncImagePainter(
                            "http://10.0.2.2:3000$oldPhotoUrl"
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DarkTeal)
            ) {
                Text("Simpan Perubahan")
            }
        }
    }

    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Pilih Foto") },
            confirmButton = {
                TextButton(onClick = {
                    showImagePickerDialog = false
                    galleryLauncher.launch("image/*")
                }) { Text("Galeri") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImagePickerDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) { Text("Kamera") }
            }
        )
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Simpan perubahan?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false

                    scope.launch {
                        try {
                            val token = tokenManager.token.first()

                            val titleBody = title.toRequestBody("text/plain".toMediaType())
                            val descBody = description.toRequestBody("text/plain".toMediaType())
                            val locBody = location.toRequestBody("text/plain".toMediaType())

                            val photoPart = imageUri?.let {
                                val file = uriToFile(it, context)
                                val reqFile = file.asRequestBody("image/*".toMediaType())
                                MultipartBody.Part.createFormData(
                                    "photo",
                                    file.name,
                                    reqFile
                                )
                            }

                            RetrofitClient.instance.updateReport(
                                token = "Bearer $token",
                                id = laporanId,
                                title = titleBody,
                                description = descBody,
                                location = locBody,
                                photo = photoPart
                            )

                            Toast.makeText(
                                context,
                                "Laporan berhasil diperbarui",
                                Toast.LENGTH_SHORT
                            ).show()

                            navController.navigateUp()

                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Gagal menyimpan perubahan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}