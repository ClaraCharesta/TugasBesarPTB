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
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
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
    userId: Int,
    jadwalId: Int,
    nama: String,
    tanggal: LocalDate
) {

    // ===================== WARNA =====================
    val backgroundColor = Color(0xFFFFE7C2)
    val cardColor = Color(0xFF9DBEBB)
    val buttonColor = Color(0xFF325B5C)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var bitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    val isFotoAda = bitmap != null


    // ===================== AMBIL FOTO =====================
    val takePhotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            val stream: InputStream? =
                context.contentResolver.openInputStream(photoUri!!)
            bitmap = BitmapFactory.decodeStream(stream)?.asImageBitmap()
            stream?.close()

            scope.launch {
                snackbarHostState.showSnackbar("Foto berhasil diambil")
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = createImageFile(context)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            imageFile = file
            photoUri = uri
            takePhotoLauncher.launch(uri)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Izin kamera ditolak")
            }
        }
    }

    // ===================== UPLOAD KE BACKEND =====================
    fun uploadToBackend() {
        if (imageFile == null) {
            scope.launch {
                snackbarHostState.showSnackbar("Foto belum diambil!")
            }
            return
        }

        val client = OkHttpClient()

        val fileBody =
            imageFile!!.asRequestBody("image/jpeg".toMediaType())

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("jadwalId", jadwalId.toString())
            .addFormDataPart("userId", userId.toString()) // 🔥 WAJIB INI
            .addFormDataPart(
                "foto",
                imageFile!!.name,
                fileBody
            )
            .build()


        val request = Request.Builder()
            .url("http://10.0.2.2:3000/api/piket/selesai")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                scope.launch {
                    snackbarHostState.showSnackbar("Gagal terhubung ke server")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                scope.launch {
                    if (response.isSuccessful) {
                        snackbarHostState.showSnackbar("Piket berhasil diselesaikan")
                        navController.popBackStack()
                    } else {
                        snackbarHostState.showSnackbar(
                            "Upload gagal (${response.code})"
                        )
                    }
                }
            }
        })
    }

    // ===================== UI =====================
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum Dikerjakan", fontSize = 22.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = backgroundColor
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(cardColor)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Nama : $nama", color = Color.White, fontSize = 18.sp)
                Text(
                    "Tanggal : ${
                        tanggal.format(
                            DateTimeFormatter.ofPattern("dd - MM - yyyy")
                        )
                    }",
                    color = Color.White,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

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
                            enter = fadeIn(tween(600)) + scaleIn(tween(600))
                        ) {
                            Image(
                                bitmap = bitmap!!,
                                contentDescription = "Foto Piket",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        Text("Foto belum ada", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ambil Foto", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { uploadToBackend() },
                    enabled = isFotoAda, // 🔥 LOGIKA UTAMA
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFotoAda) buttonColor else Color.Gray,
                        disabledContainerColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isFotoAda) "Tandai Selesai" else "Ambil Foto Terlebih Dahulu",
                        color = Color.White
                    )
                }

            }
        }
    }
}

// ===================== CREATE FILE =====================
fun createImageFile(context: Context): File {
    return File.createTempFile(
        "piket_${UUID.randomUUID()}",
        ".jpg",
        context.cacheDir
    )
}
