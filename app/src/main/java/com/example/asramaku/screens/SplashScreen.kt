package com.example.asramaku.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asramaku.R
import com.example.asramaku.navigation.Screen

@Composable
fun SplashScreen(navController: NavController) {
    val backgroundColor = Color(0xFFFFE7C2)
    val buttonColor = Color(0xFF91C9C0)
    val textColor = Color(0xFF324E52)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.school_icon),
                contentDescription = "App Illustration",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 40.dp)
            )

            Button(
                onClick = { navController.navigate(Screen.Welcome.route) },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Get Started",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "I already have an account",
                    color = textColor,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Login.route)
                    }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "→",
                    color = textColor,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
        }
    }
}
