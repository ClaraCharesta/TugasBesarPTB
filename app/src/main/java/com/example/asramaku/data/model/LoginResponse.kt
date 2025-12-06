package com.example.asramaku.data.model

data class LoginResponse(
    val message: String? = null,
    val token: String? = null,
    val user: User? = null
)
