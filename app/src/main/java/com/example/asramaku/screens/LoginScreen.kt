package com.example.asramaku.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asramaku.R
import com.example.asramaku.navigation.Screen
import com.example.asramaku.data.remote.RetrofitClient
import com.example.asramaku.data.model.LoginRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(navController: NavController) {
    val backgroundColor = Color(0xFFFFE7C2)
    val darkButtonColor = Color(0xFF2D6A6A)
    val lightButtonColor = Color(0xFF91C9C0)
    val textColor = Color(0xFF324E52)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    var showLogo by remember { mutableStateOf(false) }
    var showForm by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showLogo = true
        delay(150)
        showForm = true
        delay(200)
        showButtons = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp)
        ) {
            // 🔹 Logo
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.school_icon),
                    contentDescription = "Login Illustration",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 30.dp)
                )
            }

            // 🔹 Input Fields
            AnimatedVisibility(
                visible = showForm,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        modifier = Modifier
                            .padding(horizontal = 40.dp, vertical = 8.dp)
                            .fillMaxWidth(0.85f)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (passwordVisible)
                                painterResource(id = R.drawable.ic_visibility_off)
                            else
                                painterResource(id = R.drawable.ic_visibility)
                            Icon(
                                painter = icon,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { passwordVisible = !passwordVisible }
                            )
                        },
                        modifier = Modifier
                            .padding(horizontal = 40.dp, vertical = 4.dp)
                            .fillMaxWidth(0.85f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Forgot Password?",
                        color = textColor,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Buttons
            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = {
                            scope.launch {

                                // 🔥 Panggil API login
                                val response = try {
                                    RetrofitClient.instance.login(
                                        LoginRequest(email, password)
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Tidak dapat terhubung ke server", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }

                                if (response.isSuccessful) {

                                    val body = response.body()

                                    if (body != null) {
                                        Toast.makeText(context, "Login sukses", Toast.LENGTH_SHORT).show()

                                        // 🔥 Navigasi ke Home
                                        navController.navigate(
                                            Screen.Home.route + "?userName=${body.user.name}"
                                        ) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    }

                                } else {
                                    Toast.makeText(context, "Email atau password salah", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = darkButtonColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(horizontal = 40.dp, vertical = 4.dp)
                            .fillMaxWidth(0.85f)
                            .height(50.dp)
                    ) {
                        Text("LOGIN", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { navController.navigate(Screen.SignUp.route) },
                        colors = ButtonDefaults.buttonColors(containerColor = lightButtonColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(horizontal = 40.dp, vertical = 4.dp)
                            .fillMaxWidth(0.85f)
                            .height(50.dp)
                    ) {
                        Text("SIGN UP", color = textColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
