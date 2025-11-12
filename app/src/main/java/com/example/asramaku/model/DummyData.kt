package com.example.asramaku.model

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

// ✅ SlotData dipakai di jadwal piket
data class SlotData(
    val nama: String,
    val tanggal: String
)

object DummyData {
    // 👤 Dummy user
    val users = mutableListOf(
        User("Clara", "clara@mail.com", "12345"),
        User("Admin", "admin@mail.com", "admin")
    )

    // 💰 Dummy pembayaran
    val pembayaranList = mutableListOf(
        PembayaranData("Clara", "Oktober", "12A", "Rp 500.000", "Lunas"),
        PembayaranData("Budi", "November", "14B", "Rp 500.000", "Belum Lunas")
    )

    // 🧹 Dummy slot piket
    val slotPiketList = listOf(
        SlotData("Clara", "21 - 10 - 2025"),
        SlotData("Budi", "23 - 10 - 2025"),
        SlotData("Dina", "25 - 10 - 2025")
    )
}
