package com.example.asramaku.repository

import com.example.asramaku.data.model.LoginRequest
import com.example.asramaku.data.remote.RetrofitClient

class AuthRepository {

    suspend fun login(email: String, password: String) =
        RetrofitClient.instance.login(LoginRequest(email, password))
}