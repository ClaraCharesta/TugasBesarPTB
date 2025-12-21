package com.example.asramaku.data.remote

data class FcmPaymentRequest(
    val userId: Int,
    val bulan: String,
    val totalTagihan: Int
)