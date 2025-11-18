package com.example.asramaku.navigation

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.asramaku.model.PembayaranData
import com.example.asramaku.screens.*
import com.example.asramaku.pembayaran.*
import com.example.asramaku.piket.JadwalPiketScreen
import com.example.asramaku.piket.jadwalpiket.BelumDikerjakanScreen
import com.example.asramaku.piket.rekap.DetailPiketSayaScreen
import com.example.asramaku.piket.RekapPiketSayaScreen
import com.example.asramaku.piket.jadwalpiket.GantiPiketScreen
import com.example.asramaku.piket.slot.AmbilSlotScreen
import java.time.LocalDate

// Tambahan import modul laporan
import com.example.asramaku.laporan.*
import com.example.asramaku.model.*
import com.example.asramaku.ui.theme.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Login : Screen("login_screen")
    object SignUp : Screen("signup_screen")
    object Welcome : Screen("welcome_screen")

    object Home : Screen("home_screen/{userName}") {
        fun createRoute(userName: String) = "home_screen/$userName"
    }

    object Duty : Screen("duty_screen")
    object Report : Screen("report_screen")
    object Payment : Screen("payment_screen")
    object JadwalPiket : Screen("jadwal_piket_screen")

    object BelumDikerjakan : Screen("belum_dikerjakan_screen/{nama}/{tanggal}") {
        fun createRoute(nama: String, tanggal: String) =
            "belum_dikerjakan_screen/$nama/$tanggal"
    }

    // Diperbaiki: route DetailPiketSaya biar fleksibel (fotoUri opsional)
    object DetailPiketSaya : Screen("detail_piket_screen/{nama}/{tanggal}?fotoUri={fotoUri}") {
        fun createRoute(nama: String, tanggal: String, fotoUri: String? = null): String {
            return if (!fotoUri.isNullOrEmpty()) {
                "detail_piket_screen/$nama/$tanggal?fotoUri=$fotoUri"
            } else {
                "detail_piket_screen/$nama/$tanggal"
            }
        }
    }

    object GantiPiket : Screen("ganti_piket_screen/{nama}/{tanggal}") {
        fun createRoute(nama: String, tanggal: String) =
            "ganti_piket_screen/$nama/$tanggal"
    }

    object RekapPiket : Screen("rekap_piket_screen")

    object AmbilSlot : Screen("ambil_slot_screen")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController) {

    val riwayatPembayaranList = remember { mutableStateListOf<PembayaranData>() }
    val daftarTagihan = remember { mutableStateListOf("Oktober", "November", "Desember") }
    val statusLunasList = remember { mutableStateListOf<String>() }


    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // =====================================================
        // BAGIAN ASLI
        // =====================================================

        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(route = Screen.SignUp.route) {
            SignUpScreen(navController = navController)
        }

        composable(route = Screen.Welcome.route) {
            WelcomeScreen(navController = navController)
        }

        composable(
            route = Screen.Home.route,
            arguments = listOf(
                navArgument("userName") { type = NavType.StringType; defaultValue = "User" }
            )
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "User"
            HomeScreen(navController = navController, userName = userName)
        }

        composable(route = Screen.Duty.route) {
            DutyModuleScreen(navController = navController)
        }

        composable(route = Screen.JadwalPiket.route) {
            JadwalPiketScreen(navController = navController)
        }

        composable(route = Screen.RekapPiket.route) {
            RekapPiketSayaScreen(navController = navController)
        }

        composable(
            route = Screen.BelumDikerjakan.route,
            arguments = listOf(
                navArgument("nama") { type = NavType.StringType },
                navArgument("tanggal") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nama = backStackEntry.arguments?.getString("nama") ?: "User"
            val tanggalStr =
                backStackEntry.arguments?.getString("tanggal") ?: LocalDate.now().toString()
            val tanggal = LocalDate.parse(tanggalStr)

            BelumDikerjakanScreen(
                navController = navController,
                nama = nama,
                tanggal = tanggal,
                onSelesai = { }
            )
        }

        // Bagian ini diperbaiki biar route & argumen sinkron
        composable(
            route = "detail_piket_screen/{nama}/{tanggal}?fotoUri={fotoUri}",
            arguments = listOf(
                navArgument("nama") { type = NavType.StringType },
                navArgument("tanggal") { type = NavType.StringType },
                navArgument("fotoUri") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val nama = backStackEntry.arguments?.getString("nama") ?: "User"
            val tanggalStr =
                backStackEntry.arguments?.getString("tanggal") ?: LocalDate.now().toString()
            val tanggal = LocalDate.parse(tanggalStr)
            val fotoUri = backStackEntry.arguments?.getString("fotoUri")

            DetailPiketSayaScreen(
                navController = navController,
                nama = nama,
                tanggal = tanggal,
                fotoUri = if (fotoUri.isNullOrEmpty()) null else fotoUri
            )
        }

        composable(
            route = Screen.GantiPiket.route,
            arguments = listOf(
                navArgument("nama") { type = NavType.StringType },
                navArgument("tanggal") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nama = backStackEntry.arguments?.getString("nama") ?: "User"
            val tanggalStr =
                backStackEntry.arguments?.getString("tanggal") ?: LocalDate.now().toString()
            val tanggal = LocalDate.parse(tanggalStr)

            GantiPiketScreen(
                navController = navController,
                nama = nama,
                tanggal = tanggal
            )
        }

        // Halaman Ambil Slot
        composable(route = Screen.AmbilSlot.route) {
            AmbilSlotScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Report.route) {
            ReportScreen(navController = navController)
        }

        composable(route = Screen.Payment.route) {
            PaymentScreen(navController = navController)
        }

        // =====================================================
        // FITUR PEMBAYARAN
        // =====================================================

        composable("daftar_tagihan") {
            PaymentModuleScreen(navController, daftarTagihan)
        }

        composable("konfirmasi_pembayaran") {
            KonfirmasiPembayaranScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onSubmitClick = { _, _, _, _, _ -> },
                onCancelClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "konfirmasi_pembayaran/{bulan}/{nama}/{noKamar}/{totalTagihan}",
            arguments = listOf(
                navArgument("bulan") { type = NavType.StringType },
                navArgument("nama") { type = NavType.StringType },
                navArgument("noKamar") { type = NavType.StringType },
                navArgument("totalTagihan") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val context = LocalContext.current
            val bulan = backStackEntry.arguments?.getString("bulan") ?: ""
            val nama = backStackEntry.arguments?.getString("nama") ?: ""
            val noKamar = backStackEntry.arguments?.getString("noKamar") ?: ""
            val totalTagihan = backStackEntry.arguments?.getString("totalTagihan") ?: ""

            KonfirmasiPembayaranScreen(
                navController = navController,
                bulan = bulan,
                nama = nama,
                noKamar = noKamar,
                totalTagihan = totalTagihan,
                onBackClick = { navController.popBackStack() },
                onSubmitClick = { namaInput, bulanInput, noKamarInput, totalInput, buktiUri ->
                    if (
                        namaInput.isBlank() || bulanInput.isBlank() ||
                        noKamarInput.isBlank() || totalInput.isBlank() || buktiUri == null
                    ) {
                        Toast.makeText(
                            context,
                            "Harap isi semua data terlebih dahulu!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Pembayaran berhasil!",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (riwayatPembayaranList.none { it.bulan == bulanInput }) {
                            riwayatPembayaranList.add(
                                PembayaranData(
                                    nama = namaInput,
                                    bulan = bulanInput,
                                    noKamar = noKamarInput,
                                    totalTagihan = totalInput,
                                    status = "Lunas",
                                    buktiUri = buktiUri.toString()
                                )
                            )
                            daftarTagihan.remove(bulanInput)
                            // TANDAI BULAN INI PERMANEN LUNAS
                            if (!statusLunasList.contains(bulanInput)) {
                                statusLunasList.add(bulanInput)
                            }
                        }

                        navController.navigate("riwayat_pembayaran")
                    }
                },
                onCancelClick = { navController.popBackStack() }
            )
        }

        composable("status_pembayaran") {
            val dataStatus = listOf("Oktober", "November", "Desember").map { bulanItem ->
                Triple(
                    bulanItem,
                    "500000",
                    if (statusLunasList.contains(bulanItem)) "Lunas" else "Belum Lunas"
                )
            }

            StatusPembayaranScreen(
                navController = navController,
                riwayatList = dataStatus
            )
        }

        composable("riwayat_pembayaran") {
            RiwayatPembayaranScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                riwayatList = riwayatPembayaranList,
                onDetailClick = { index ->
                    navController.navigate("detail_pembayaran/$index")
                },
                onDeleteItem = { index ->
                    riwayatPembayaranList.removeAt(index)
                }
            )
        }

        composable(
            route = "detail_pembayaran/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: -1
            val pembayaran = riwayatPembayaranList.getOrNull(index)
            if (pembayaran != null) {
                DetailPembayaranScreen(
                    pembayaran = pembayaran,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                Text("Data tidak ditemukan")
            }
        }

        // =====================================================
        // FITUR LAPORAN KERUSAKAN
        // =====================================================

        composable("dashboard") {
            ReportScreen(navController = navController)
        }

        composable("buat_laporan") {
            BuatLaporan(navController = navController)
        }

        composable("daftar_laporan") {
            DaftarLaporan(navController = navController)
        }

        composable(
            route = "detail_laporan/{laporanId}",
            arguments = listOf(
                navArgument("laporanId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val laporanId = backStackEntry.arguments?.getString("laporanId") ?: ""
            DetailLaporan(navController = navController, laporanId = laporanId)
        }

        composable(
            route = "edit_laporan/{laporanId}",
            arguments = listOf(
                navArgument("laporanId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val laporanId = backStackEntry.arguments?.getString("laporanId") ?: ""
            EditLaporan(navController = navController, laporanId = laporanId)
        }

        composable("notifikasi") {
            Notifikasi(navController = navController)
        }
    }
}
