package com.example.asramaku.data.model

data class DetailPembayaranResponse(
    val id: Int,
    val userId: Int,
    val bulan: String,
    val totalTagihan: Int,
    val status: String,
    val buktiBayar: String?
)