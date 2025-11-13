package com.example.asramaku.model

data class Laporan(
    val id: String,
    val judulKerusakan: String,
    val deskripsiKerusakan: String,
    val lokasiKamar: String,
    val fotoUrl: String?,
    val tanggal: String,
    val status: String = "Menunggu" // Status: Menunggu, Diproses, Selesai
)
