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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.asramaku.R
import com.example.asramaku.data.local.TokenManager
import com.example.asramaku.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(navController: NavController, vm: LoginViewModel = viewModel()) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }   // ✅ TAMBAHAN SAJA

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
        modifier = Modifier.fillMaxSize().background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ======================= LOGO =======================
            AnimatedVisibility(visible = showLogo, enter = fadeIn(), exit = fadeOut()) {
                Image(
                    painter = painterResource(id = R.drawable.school_icon),
                    contentDescription = "Login",
                    modifier = Modifier.size(200.dp).padding(bottom = 30.dp)
                )
            }

            // ======================= FORM INPUT =======================
            AnimatedVisibility(visible = showForm, enter = fadeIn(), exit = fadeOut()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.padding(horizontal = 40.dp).fillMaxWidth(0.85f)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation =
                            if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    if (passwordVisible) R.drawable.ic_visibility_off
                                    else R.drawable.ic_visibility
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp).clickable {
                                    passwordVisible = !passwordVisible
                                }
                            )
                        },
                        modifier = Modifier
                            .padding(horizontal = 40.dp, vertical = 8.dp)
                            .fillMaxWidth(0.85f)
                    )

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ======================= BUTTON LOGIN =======================
            AnimatedVisibility(visible = showButtons, enter = fadeIn(), exit = fadeOut()) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Button(
                        onClick = {
                            if (isLoading) return@Button   // ✅ CEGAH DOUBLE KLIK
                            isLoading = true

                            vm.login(email, password) { success, userId, userName, tokenOrMessage ->

                                isLoading = false

                                if (success) {
                                    val token = tokenOrMessage

                                    scope.launch {
                                        tokenManager.saveSession(
                                            token = token,
                                            userId = userId,
                                            userName = userName
                                        )
                                    }

                                    Toast.makeText(context, "Login sukses", Toast.LENGTH_SHORT).show()

                                    navController.navigate(
                                        "home_screen/$userId/$userName"   // ✅ FIX ROUTE
                                    ) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }

                                } else {
                                    Toast.makeText(context, tokenOrMessage, Toast.LENGTH_SHORT).show()

                            }
                            }
                        },
                        enabled = !isLoading,   // ✅ TOMBOL TERKUNCI SAAT LOGIN
                        colors = ButtonDefaults.buttonColors(containerColor = darkButtonColor),
                        modifier = Modifier.fillMaxWidth(0.85f).height(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("LOGIN", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = { if (!isLoading) navController.navigate(Screen.SignUp.route) },
                        colors = ButtonDefaults.buttonColors(containerColor = lightButtonColor),
                        modifier = Modifier.fillMaxWidth(0.85f).height(50.dp)
                    ) {
                        Text("SIGN UP", color = textColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
