package com.example.asramaku.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asramaku.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repo = AuthRepository()

    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repo.login(email, password)

                if (response.isSuccessful) {
                    onResult(true, response.body()?.user?.name ?: "")
                } else {
                    onResult(false, "Email atau password salah")
                }

            } catch (e: Exception) {
                onResult(false, "Tidak dapat terhubung ke server")
            }
        }
    }
}