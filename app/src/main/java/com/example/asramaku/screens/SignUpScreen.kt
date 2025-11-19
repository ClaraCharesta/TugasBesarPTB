package com.example.asramaku.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asramaku.R
import com.example.asramaku.model.UserPreferences
import com.example.asramaku.navigation.Screen
import com.example.asramaku.data.remote.RetrofitClient
import com.example.asramaku.data.model.RegisterRequest
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val backgroundColor = Color(0xFFFFE7C2)
    val buttonColor = Color(0xFF91C9C0)
    val textColor = Color(0xFF324E52)

    // Animasi muncul
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                initialOffsetY = { it / 6 },
                animationSpec = tween(800)
            ),
            exit = fadeOut(animationSpec = tween(400)) + slideOutVertically(
                targetOffsetY = { it / 6 },
                animationSpec = tween(400)
            )
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Gambar / Icon
                Image(
                    painter = painterResource(id = R.drawable.school_icon),
                    contentDescription = "App Illustration",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(bottom = 24.dp)
                )

                // Judul
                Text(
                    text = "Create Account",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sign up to get started with AsramaKu",
                    fontSize = 16.sp,
                    color = textColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Email input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Password input
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Image(
                                painter = painterResource(id = icon),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(textColor)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Sign Up Button (fungsional)
                Button(
                    onClick = {
                        if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                            scope.launch {
                                val response = try {
                                    RetrofitClient.instance.register(
                                        RegisterRequest(
                                            name = name,
                                            email = email,
                                            password = password
                                        )
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Tidak dapat terhubung ke server", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }

                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show()

                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.SignUp.route) { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, "Gagal membuat akun", Toast.LENGTH_SHORT).show()
                                }
                            }

                        } else {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Sign Up",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Already have account
                TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                    Text(
                        text = "Already have an account? Login",
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
