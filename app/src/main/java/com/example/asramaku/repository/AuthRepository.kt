package com.example.asramaku.repository

import com.example.asramaku.data.model.LoginRequest
import com.example.asramaku.data.model.LoginResponse
import com.example.asramaku.data.remote.RetrofitClient
import retrofit2.Response   // <-- WAJIB GANTI INI

class AuthRepository {

    suspend fun login(email: String, password: String) : Response<LoginResponse> {
        println("AuthRepository: login request -> $email")
        val res = RetrofitClient.instance.login(LoginRequest(email, password))
        println("AuthRepository: response code = ${res.code()}")
        return res
    }
}
