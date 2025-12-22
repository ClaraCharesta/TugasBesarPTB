package com.example.asramaku.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.asramaku.R
import com.example.asramaku.data.local.TokenManager
import com.example.asramaku.navigation.Screen
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import com.example.asramaku.data.remote.FcmPiketRequest
import com.example.asramaku.data.remote.FcmPiketService
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    navController: NavController,
    vm: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val backgroundColor = Color(0xFFFFE7C2)
    val darkButtonColor = Color(0xFF2D6A6A)
    val lightButtonColor = Color(0xFF91C9C0)
    val textColor = Color(0xFF324E52)
    val scrollState = rememberScrollState()

    var showLogo by remember { mutableStateOf(false) }
    var showForm by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showLogo = true
        showForm = true
        showButtons = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            AnimatedVisibility(visible = showLogo, enter = fadeIn(), exit = fadeOut()) {
                Image(
                    painter = painterResource(id = R.drawable.school_icon),
                    contentDescription = "Login",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 30.dp)
                )
            }


            AnimatedVisibility(visible = showForm, enter = fadeIn(), exit = fadeOut()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier
                            .padding(horizontal = 40.dp)
                            .fillMaxWidth(0.85f)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation =
                            if (passwordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    if (passwordVisible)
                                        R.drawable.ic_visibility_off
                                    else
                                        R.drawable.ic_visibility
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { passwordVisible = !passwordVisible }
                            )
                        },
                        modifier = Modifier
                            .padding(horizontal = 40.dp, vertical = 8.dp)
                            .fillMaxWidth(0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            AnimatedVisibility(visible = showButtons, enter = fadeIn(), exit = fadeOut()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Button(
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = darkButtonColor),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(50.dp),
                        onClick = {
                            if (isLoading) return@Button
                            isLoading = true

                            vm.login(email, password) { success, userId, userName, messageOrToken ->
                                isLoading = false

                                if (success) {
                                    scope.launch {
                                        tokenManager.saveSession(
                                            token = messageOrToken,
                                            userId = userId,
                                            userName = userName
                                        )


                                        FirebaseMessaging.getInstance().token
                                            .addOnSuccessListener { fcmToken ->


                                                scope.launch {
                                                    try {
                                                        FcmPiketService.api.sendToken(
                                                            FcmPiketRequest(
                                                                userId = userId,
                                                                fcmToken = fcmToken
                                                            )
                                                        )
                                                        Log.d(
                                                            "FCM_PIKET",
                                                            "Token piket berhasil dikirim"
                                                        )
                                                    } catch (e: Exception) {
                                                        Log.e(
                                                            "FCM_PIKET_FAIL",
                                                            e.message.toString()
                                                        )
                                                    }
                                                }


                                                sendPaymentToken(userId, fcmToken)


                                                sendReportToken(userId, fcmToken)
                                            }
                                    }

                                    Toast.makeText(
                                        context,
                                        "Login sukses",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    navController.navigate(
                                        "home_screen/$userId/$userName"
                                    ) {
                                        popUpTo(Screen.Login.route) {
                                            inclusive = true
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        messageOrToken,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "LOGIN",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (!isLoading) {
                                navController.navigate(Screen.SignUp.route)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = lightButtonColor),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "SIGN UP",
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


private fun sendPaymentToken(userId: Int, token: String) {
    val json = JSONObject().apply {
        put("userId", userId)
        put("fcmToken", token)
    }

    val body = json.toString()
        .toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url("http://10.0.2.2:3000/api/fcm/payment/token")
        .post(body)
        .build()

    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("FCM_PAYMENT_FAIL", "Failed to send payment token: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            Log.d("FCM_PAYMENT_OK", "Payment token sent, response code: ${response.code}")
        }
    })
}


private fun sendReportToken(userId: Int, token: String) {
    val json = JSONObject().apply {
        put("userId", userId)
        put("fcmToken", token)
    }

    val body = json.toString()
        .toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url("http://10.0.2.2:3000/api/fcm/report/token")
        .post(body)
        .build()

    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("FCM_REPORT_FAIL", "Failed to send report token: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            Log.d("FCM_REPORT_OK", "Report token sent, response code: ${response.code}")
        }
    })
}
