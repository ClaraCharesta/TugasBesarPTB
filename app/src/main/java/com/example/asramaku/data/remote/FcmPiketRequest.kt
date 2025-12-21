package com.example.asramaku.data.remote

// ================= REQUEST BODY =================
data class FcmPiketRequest(
    val userId: Int,
    val fcmToken: String
)
