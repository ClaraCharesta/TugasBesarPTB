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

// SlotData dipakai di jadwal piket
data class SlotData(
    val nama: String,
    val tanggal: String
)

object DummyData {
    // Dummy user
    val users = mutableListOf(
        User("Clara", "clara@mail.com", "12345"),
        User("Admin", "admin@mail.com", "admin")
    )

    // Dummy pembayaran
    val pembayaranList = mutableListOf(
        PembayaranData("Clara", "Oktober", "12A", "Rp 500.000", "Lunas"),
        PembayaranData("Budi", "November", "14B", "Rp 500.000", "Belum Lunas")
    )

    // Dummy slot piket

    // Daftar slot piket yang bisa diambil
    val slotPiketList = mutableStateListOf(
        SlotData("", "2025-10-28"),
        SlotData("", "2025-10-30"),
        SlotData("", "2025-10-31"),
        SlotData("", "2026-11-02"),
        SlotData("", "2026-11-05")
    )

    val jadwalUserMap = mutableStateMapOf<String, String>()

    val daftarLaporan = mutableStateListOf(
        Laporan(
            id = "1",
            judulKerusakan = "Lampu Kamar Rusak",
            deskripsiKerusakan = "Lampu kamar kedip-kedip kemudian tidak menyala",
            lokasiKamar = "Kamar B-12",
            fotoUrl = null,
            tanggal = "16 oktober 2025",
            status = "Menunggu"
        ),
        Laporan(
            id = "2",
            judulKerusakan = "Keran Air Bocor",
            deskripsiKerusakan = "Keran air di kamar mandi bocor terus menerus",
            lokasiKamar = "Kamar B-12",
            fotoUrl = null,
            tanggal = "09 Oktober 2025",
            status = "Diproses"
        )
    )

    val daftarNotifikasi = mutableStateListOf(
        Notifikasi(
            id = "1",
            judul = "Laporan berhasil dikirim",
            pesan = "Laporan 'Lampu Kamar Rusak' telah dikirim",
            waktu = "2 jam yang lalu",
            tipe = "kirim"
        ),
        Notifikasi(
            id = "2",
            judul = "Laporan dihapus",
            pesan = "Laporan 'Meja Rusak' telah dihapus",
            waktu = "5 jam yang lalu",
            tipe = "hapus"
        ),
        Notifikasi(
            id = "3",
            judul = "Laporan diubah",
            pesan = "Laporan 'Keran Air' telah diubah",
            waktu = "1 hari yang lalu",
            tipe = "edit"
        )
    )
}
