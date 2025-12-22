package com.example.asramaku.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST




interface FcmPiketApi {

    @POST("api/fcm/piket")
    suspend fun sendToken(
        @Body request: FcmPiketRequest
    )
}


object FcmPiketService {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:3000/") // emulator
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: FcmPiketApi = retrofit.create(FcmPiketApi::class.java)
}
