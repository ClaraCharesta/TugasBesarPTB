@file:Suppress("LABEL_NAME_CLASH")

package com.example.asramaku.piket.jadwalpiket

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BelumDikerjakanScreen(
    navController: NavController,
    nama: String,
    tanggal: LocalDate,
    onSelesai: (LocalDate) -> Unit
) {
    val backgroundColor = Color(0xFFFFE7C2)
    val cardColor = Color(0xFF9DBEBB)
    val buttonColor = Color(0xFF325B5C)
    val context = LocalContext.current

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Launcher untuk ambil foto
    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            // Load bitmap dari URI
            val inputStream: InputStream? = context.contentResolver.openInputStream(photoUri!!)
            bitmap = BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
            inputStream?.close()

            coroutineScope.launch {
                snackbarHostState.showSnackbar("Foto berhasil diambil")
            }
        }
    }

    // Launcher untuk permission kamera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = photoFile(context)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            photoUri = uri
            takePhotoLauncher.launch(uri)
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Izin kamera ditolak")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum Dikerjakan",
                            fontSize = 22.sp,
                            color = Color.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(cardColor)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Nama : $nama", color = Color.White, fontSize = 18.sp)
                Text(
                    text = "Tanggal : ${tanggal.format(DateTimeFormatter.ofPattern("dd - MM - yyyy"))}",
                    color = Color.White,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Placeholder foto dengan animasi
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFD8CFC4)),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        this@Column.AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(600)) + scaleIn(animationSpec = tween(600))
                        ) {
                            Image(
                                bitmap = bitmap!!,
                                contentDescription = "Foto Piket",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(20.dp))
                            )
                        }
                    } else {
                        Text(text = "Foto belum ada", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Ambil Foto", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSelesai(tanggal)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Piket telah selesai")
                        }
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Tandai Selesai", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

// Helper buat file sementara foto
fun photoFile(context: Context): File {
    val dir = context.cacheDir
    return File.createTempFile(
        "piket_${UUID.randomUUID()}",
        ".jpg",
        dir
    )
}
