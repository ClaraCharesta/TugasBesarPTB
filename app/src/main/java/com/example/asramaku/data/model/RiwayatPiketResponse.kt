package com.example.asramaku.data.model

data class RiwayatPiketResponse(
    val id: Int,
    val userId: Int,
    val jadwalPiketId: Int,
    val tanggal: String,
    val foto: String?
)
