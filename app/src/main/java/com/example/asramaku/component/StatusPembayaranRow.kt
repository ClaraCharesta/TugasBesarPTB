package com.example.asramaku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun StatusPembayaranRow(no: Int, bulan: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(Color.White)
            .border(1.dp, Color.Black),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableCell(no.toString(), 0.15f)
        VerticalDivider()
        TableCell(bulan, 0.45f)
        VerticalDivider()
        StatusCell(status, 0.4f)
    }
}

@Composable
fun RowScope.TableCell(text: String, weight: Float) {
    Box(
        modifier = Modifier
            .weight(weight)
            .fillMaxHeight()
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RowScope.StatusCell(status: String, weight: Float) {
    val warnaBg = if (status.equals("Lunas", ignoreCase = true)) Color(0xFF2E6664) else Color(0xFF8AA09B)
    Box(
        modifier = Modifier
            .weight(weight)
            .fillMaxHeight()
            .padding(horizontal = 6.dp, vertical = 6.dp), // memberi jarak dari garis tepi
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = warnaBg,
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = status,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun VerticalDivider() {
    Divider(
        color = Color.Black,
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
    )
}
