package com.example.asramaku.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmPaymentApi {

    @POST("api/fcm/payment/reminder")
    suspend fun sendPaymentReminder(
        @Body request: FcmPaymentRequest
    )
}

object FcmPaymentService {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.5:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: FcmPaymentApi = retrofit.create(FcmPaymentApi::class.java)
}