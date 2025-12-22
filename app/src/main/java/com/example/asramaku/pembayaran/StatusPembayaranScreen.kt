package com.example.asramaku.pembayaran

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asramaku.component.StatusPembayaranRow
import com.example.asramaku.screens.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusPembayaranScreen(
    navController: NavController,
    viewModel: PaymentViewModel,
    userId: Int
) {


    val statusList by viewModel.statusList.collectAsState()

    val currentRoute = navController.currentBackStackEntry
        ?.destination
        ?.route ?: ""


    LaunchedEffect(userId) {
        viewModel.loadAllStatus(userId)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Box(modifier = Modifier.weight(1f)) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Status Pembayaran",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        },
                        navigationIcon = {},
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFFAED6D3)
                        )
                    )
                },
                containerColor = Color(0xFFFFF0D5)
            ) { innerPadding ->

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFC9D6C3))
                            .border(1.dp, Color.DarkGray),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableHeaderCell("No", 0.15f)
                        TableHeaderCell("Bulan Tagihan", 0.45f)
                        TableHeaderCell("Status Pembayaran", 0.4f)
                    }


                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        itemsIndexed(statusList) { index, item ->
                            StatusPembayaranRow(
                                no = index + 1,
                                bulan = item.bulan,
                                status = item.status
                            )
                        }
                    }
                }
            }
        }


        PaymentTabMenu(
            currentRoute = currentRoute,
            navController = navController,
            userId = userId
        )

    }
}


@Composable
fun RowScope.TableHeaderCell(text: String, weight: Float) {
    Box(
        modifier = Modifier
            .weight(weight)
            .background(Color(0xFFD9E1D0))
            .border(1.dp, Color.Black)
            .height(IntrinsicSize.Min),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
