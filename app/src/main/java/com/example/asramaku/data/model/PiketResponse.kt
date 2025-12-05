package com.example.asramaku.data.model

data class PiketResponse(
    val id: Int,
    val userId: Int,
    val tanggal: String,
    val status: String? // nullable, biar aman
)