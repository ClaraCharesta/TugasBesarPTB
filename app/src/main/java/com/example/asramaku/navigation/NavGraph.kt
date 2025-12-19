package com.example.asramaku.navigation

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.asramaku.data.remote.RetrofitClient
import com.example.asramaku.model.PembayaranData
import com.example.asramaku.screens.*
import com.example.asramaku.pembayaran.*
import com.example.asramaku.piket.JadwalPiketScreen
import com.example.asramaku.piket.jadwalpiket.BelumDikerjakanScreen
import com.example.asramaku.piket.rekap.DetailPiketSayaScreen
import com.example.asramaku.piket.RekapPiketSayaScreen
import java.time.LocalDate

// Tambahan import modul laporan
import com.example.asramaku.laporan.*
import com.example.asramaku.piket.jadwalpiket.GantiPiketCalendarScreen

/**
 * Nav routes: keep base names here and provide createRoute helpers.
 * This file registers matching composable(...) routes exactly as declared below.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Login : Screen("login_screen")
    object SignUp : Screen("signup_screen")
    object Welcome : Screen("welcome_screen")

    // Home: base "home" + optional query ?userName=
    object Home : Screen("home") {
        fun createRoute(userName: String? = null): String =
            if (userName.isNullOrBlank()) "home" else "home?userName=${Uri.encode(userName)}"
        // composable routes registered: "home" and "home?userName={userName}"
    }

    // Duty expects two path params
    object Duty : Screen("duty_module_screen") {
        fun createRoute(userId: Int, nama: String) =
            "duty_module_screen/$userId/${Uri.encode(nama)}"
    }

    object Report : Screen("report_screen")


    object JadwalPiket : Screen("jadwal_piket_screen/{userId}/{nama}") {
        fun createRoute(userId: Int, nama: String) = "jadwal_piket_screen/$userId/${Uri.encode(nama)}"
    }

    object BelumDikerjakan :
        Screen("belum_dikerjakan_screen/{userId}/{jadwalId}/{nama}/{tanggal}") {

        fun createRoute(
            userId: Int,
            jadwalId: Int,
            nama: String,
            tanggal: String
        ) = "belum_dikerjakan_screen/$userId/$jadwalId/${Uri.encode(nama)}/$tanggal"
    }




    object DetailPiketSaya : Screen("detail_piket_screen/{tanggal}?userId={userId}") {
        fun createRoute(tanggal: String, userId: Int? = null): String {
            return if (userId != null && userId != 0) {
                "detail_piket_screen/$tanggal?userId=$userId"
            } else {
                "detail_piket_screen/$tanggal"
            }
        }
    }

    object GantiPiket : Screen("ganti_piket_screen/{nama}/{piketId}/{tanggal}") {
        fun createRoute(nama: String, piketId: Int, tanggal: String) =
            "ganti_piket_screen/${Uri.encode(nama)}/$piketId/${Uri.encode(tanggal)}"
    }





    object RekapPiket : Screen("rekap_piket_screen/{userId}/{namaLogin}") {
        fun createRoute(userId: Int, namaLogin: String) = "rekap_piket_screen/$userId/${Uri.encode(namaLogin)}"
    }



    object Payment : Screen("payment_screen/{userId}") {
        fun createRoute(userId: Int) = "payment_screen/$userId"
    }





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
        // ========================
        // AUTH
        // ========================
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(navController = navController)
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(navController = navController)
        }

        // ========================
        // HOME - two registrations:
        // 1) plain "home" (no args)
        // 2) "home?userName={userName}" (optional query param)
        // This preserves existing HomeScreen signature (navController, userName: String?)
        // so you DON'T need to modify HomeScreen file.
        // ========================
// ========================
// HOME FIX SESUAI LOGIN
// ========================
        composable(
            route = "home_screen/{userId}/{userName}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("userName") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val userName = backStackEntry.arguments?.getString("userName") ?: ""

            HomeScreen(
                navController = navController,
                userId = userId,
                userName = userName
            )
        }




        // ========================
        // DUTY - use exact route from sealed class
        // ========================
        composable(
            route = "duty_module_screen/{userId}/{nama}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("nama") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val nama = backStackEntry.arguments?.getString("nama") ?: ""

            DutyModuleScreen(
                navController = navController,
                userId = userId,
                namaLogin = nama  // <-- ini yang tadinya bikin error
            )
        }





        // ========================
        // JADWAL PIKET
        // ========================
        composable(
            route = "jadwal_piket_screen/{userId}/{nama}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("nama") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val namaLogin = backStackEntry.arguments?.getString("nama") ?: ""

            JadwalPiketScreen(navController, userId, namaLogin)
        }


        // ========================
        // REKAP PIKET
        // ========================
        composable(
            route = Screen.RekapPiket.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("namaLogin") { type = NavType.StringType }   // ⬅️ tambahkan namaLogin
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val namaLogin = backStackEntry.arguments?.getString("namaLogin") ?: ""

            RekapPiketSayaScreen(
                navController = navController,
                userId = userId,
                namaLogin = namaLogin
            )
        }




        // ========================
        // BELUM DIKERJAKAN
        // ========================
        composable(
            route = Screen.BelumDikerjakan.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("jadwalId") { type = NavType.IntType },
                navArgument("nama") { type = NavType.StringType },
                navArgument("tanggal") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val jadwalId = backStackEntry.arguments?.getInt("jadwalId") ?: 0
            val nama = backStackEntry.arguments?.getString("nama") ?: ""
            val tanggalStr = backStackEntry.arguments?.getString("tanggal") ?: ""
            val tanggal = LocalDate.parse(tanggalStr)

            BelumDikerjakanScreen(
                navController = navController,
                userId = userId,
                jadwalId = jadwalId,
                nama = nama,
                tanggal = tanggal
            )
        }



        // ========================
        // DETAIL PIKET SAYA (optional fotoUri)
        // ========================
        composable(
            route = Screen.DetailPiketSaya.route,
            arguments = listOf(
                navArgument("tanggal") { type = NavType.StringType },
                navArgument("userId") {
                    type = NavType.IntType
                    defaultValue = 0  // jangan pakai nullable
                }
            )
        ) { backStackEntry ->
            val tanggal = backStackEntry.arguments?.getString("tanggal") ?: LocalDate.now().toString()
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0

            DetailPiketSayaScreen(
                navController = navController,
                tanggal = tanggal,
                userId = userId
            )
        }


        // ========================
        // GANTI PIKET
        // ========================
        composable(
            route = "ganti_piket_screen/{nama}/{piketId}/{tanggal}",
            arguments = listOf(
                navArgument("nama") { type = NavType.StringType },
                navArgument("piketId") { type = NavType.IntType },
                navArgument("tanggal") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nama = Uri.decode(backStackEntry.arguments?.getString("nama") ?: "User")
            val piketId = backStackEntry.arguments?.getInt("piketId") ?: 0
            val tanggalStr = Uri.decode(backStackEntry.arguments?.getString("tanggal") ?: "")
            val tanggal = try { LocalDate.parse(tanggalStr) } catch (_: Exception) { LocalDate.now() }

            GantiPiketCalendarScreen(
                navController = navController,
                slotId = piketId,
                tanggalLama = tanggal
            )
        }




        // ========================
        // Report, Payment (no args)
        // ========================


        composable(route = Screen.Report.route) {
            ReportScreen(navController, userName = null)
        }

        // ============================
        // MODUL PEMBAYARAN
        // ============================
        composable(
            route = Screen.Payment.route, // route = "payment_screen/{userId}"
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vm: PaymentViewModel = viewModel(
                factory = PaymentViewModelFactory(RetrofitClient.instance)
            )

            // Ambil userId dari navigation argument
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0

            PaymentModuleScreen(
                navController = navController,
                viewModel = vm,
                userId = userId
            )
        }



        // ============================
        // KONFIRMASI PEMBAYARAN
        // ============================
        composable(
            route = "konfirmasi_pembayaran/{userId}/{bulan}/{total}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("bulan") { type = NavType.StringType },
                navArgument("total") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments!!.getInt("userId")
            val bulan = backStackEntry.arguments!!.getString("bulan")!!
            val total = backStackEntry.arguments!!.getInt("total")

            val vm: PaymentViewModel = viewModel(
                factory = PaymentViewModelFactory(RetrofitClient.instance)
            )

            KonfirmasiPembayaranScreen(
                navController = navController,
                viewModel = vm,
                userId = userId,       // ✅ sekarang diteruskan
                bulan = bulan,
                total = total
            )
        }


        // ============================
        // STATUS PEMBAYARAN
        // ============================
        composable(
            "status_pembayaran/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments!!.getInt("userId")

            val vm: PaymentViewModel = viewModel(
                factory = PaymentViewModelFactory(RetrofitClient.instance)
            )

            StatusPembayaranScreen(
                navController = navController,
                viewModel = vm,
                userId = userId // ✅ diteruskan ke screen
            )
        }


// RIWAYAT PEMBAYARAN (FIX FINAL)
// ============================
        composable("riwayat_pembayaran/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->

            val userId = backStackEntry.arguments?.getInt("userId") ?: 0

            val vm: PaymentViewModel = viewModel(
                factory = PaymentViewModelFactory(RetrofitClient.instance)
            )

            RiwayatPembayaranScreen(
                navController = navController,
                viewModel = vm,
                userId = userId,
                onDetailClick = { uId, paymentId ->
                    navController.navigate("detail_pembayaran/$uId/$paymentId")
                },
                onDeleteItem = { uId, paymentId ->
                    vm.deletePayment(paymentId, uId)
                }
            )
        }


        // ============================
// DETAIL PEMBAYARAN (FIX FINAL)
// ============================
        composable(
            route = "detail_pembayaran/{userId}/{paymentId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("paymentId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val paymentId = backStackEntry.arguments?.getInt("paymentId") ?: 0

            val vm: PaymentViewModel = viewModel(
                factory = PaymentViewModelFactory(RetrofitClient.instance)
            )

            DetailPembayaranScreen(
                navController = navController,
                userId = userId,        // ✅ lempar userId
                paymentId = paymentId,
                viewModel = vm,
                onBackClick = { navController.popBackStack() }
            )
        }


        // ========================
        // LAPORAN KERUSAKAN
        // ========================


        composable("buat_laporan") {
            BuatLaporan(navController = navController)
        }

        composable("daftar_laporan") {
            DaftarLaporan(navController = navController)
        }

        composable(
            route = "detail_laporan/{laporanId}",
            arguments = listOf(
                navArgument("laporanId") { type = NavType.IntType } // ✅ ubah jadi IntType
            )
        ) { backStackEntry ->

            val laporanId = backStackEntry.arguments?.getInt("laporanId") ?: 0

            DetailLaporan(
                navController = navController,
                laporanId = laporanId
            )
        }


        composable(
            route = "edit_laporan/{laporanId}",
            arguments = listOf(navArgument("laporanId") { type = NavType.IntType }) // ✅ ubah jadi IntType
        ) { backStackEntry ->

            val laporanId = backStackEntry.arguments?.getInt("laporanId") ?: 0 // ✅ default 0 jika null

            EditLaporan(
                navController = navController,
                laporanId = laporanId
            )
        }


    }
}
