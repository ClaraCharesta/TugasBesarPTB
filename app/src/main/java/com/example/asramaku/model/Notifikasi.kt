package com.example.asramaku.model

data class Notifikasi(
    val id: String,
    val judul: String,
    val pesan: String,
    val waktu: String,
    val tipe: String // "kirim", "edit", "hapus"
)