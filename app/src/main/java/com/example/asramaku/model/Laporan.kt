package com.example.asramaku.model

data class Laporan(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String,
    val location: String?,
    val photoUrl: String?,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)