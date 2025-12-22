package com.example.asramaku.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf

data class User(
    val name: String,
    val email: String,
    val password: String
)

data class PembayaranData(
    val nama: String,
    val bulan: String,
    val noKamar: String,
    val totalTagihan: String,
    val status: String,
    val buktiUri: String? = null
)

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)


data class SlotData(
    val nama: String,
    val tanggal: String
)

object DummyData {

    val users = mutableListOf(
        User("Clara", "clara@mail.com", "12345"),
        User("Admin", "admin@mail.com", "admin")
    )


    val pembayaranList = mutableListOf(
        PembayaranData("Clara", "Oktober", "12A", "Rp 500.000", "Lunas"),
        PembayaranData("Budi", "November", "14B", "Rp 500.000", "Belum Lunas")
    )




    val slotPiketList = mutableStateListOf(
        SlotData("", "2025-10-28"),
        SlotData("", "2025-10-30"),
        SlotData("", "2025-10-31"),
        SlotData("", "2026-11-02"),
        SlotData("", "2026-11-05")
    )

    val jadwalUserMap = mutableStateMapOf<String, String>()
}
