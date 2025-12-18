package com.example.asramaku.data.model


data class Payment(
    val id: Int,
    val userId: Int,
    val bulan: String,
    val totalTagihan: Int,
    val status: String,
    val buktiBayar: String?,
    val createdAt: String?,
    val updatedAt: String?
)
