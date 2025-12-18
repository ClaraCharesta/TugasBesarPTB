package com.example.asramaku.data.model


data class GantiPiketResponse(
    val id: Int,
    val tanggal: String,
    val shift: String,
    val takenBy: Int?,
    val isAvailable: Boolean,
    val expireAt: String?
)
