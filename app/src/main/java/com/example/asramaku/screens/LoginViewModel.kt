package com.example.asramaku.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asramaku.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repo = AuthRepository()

    // Boolean = sukses atau tidak
    // String  = nama user
    // String  = token ATAU pesan error
    fun login(email: String, password: String, onResult: (Boolean, String, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repo.login(email, password)

                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.token ?: ""
                    val name = body?.user?.name ?: ""

                    // sukses → kirim name + token
                    onResult(true, name, token)

                } else {
                    // gagal dari server (400/401)
                    onResult(false, "", "Email atau password salah")
                }

            } catch (e: Exception) {
                // gagal koneksi
                onResult(false, "", "Tidak dapat terhubung ke server")
            }
        }
    }
}