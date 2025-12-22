package com.example.asramaku.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asramaku.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repo = AuthRepository()

    /**
     * Callback format:
     *  success : Boolean          → true = berhasil login
     *  userId : Int               → ID pengguna (default 0 jika gagal)
     *  userName : String          → nama user
     *  token : String             → token atau pesan error
     */
    fun login(email: String, password: String, onResult: (Boolean, Int, String, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repo.login(email, password)


                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!


                    val userId = body.user?.id ?: 0
                    val userName = body.user?.name ?: ""
                    val token = body.token ?: ""


                    println("LOGIN OK — token=${token}, userId=${userId}, userName=${userName}")


                    onResult(true, userId, userName, token)


                    return@launch
                } else {

                    val err = try {
                        response.errorBody()?.string() ?: "Email atau password salah"
                    } catch (_: Exception) {
                        "Email atau password salah"
                    }
                    onResult(false, 0, "", err)
                    return@launch
                }

            } catch (e: Exception) {

                e.printStackTrace()
                onResult(false, 0, "", "Tidak dapat terhubung ke server")
                return@launch
            }
        }
    }
}