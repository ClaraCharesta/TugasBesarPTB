package com.example.asramaku.data.model

data class PaymentRequest(
    val userId: Int,
    val bulan: String,
    val totalTagihan: Int,
    val buktiBayar: String? // Base64
)
