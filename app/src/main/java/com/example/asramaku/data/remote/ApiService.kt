package com.example.asramaku.data.remote

import com.example.asramaku.data.model.LoginRequest
import com.example.asramaku.data.model.LoginResponse
import com.example.asramaku.data.model.RegisterRequest
import com.example.asramaku.data.model.PiketRequest
import com.example.asramaku.data.model.PiketResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // Login user
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // Register user
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<LoginResponse>

    // Get piket berdasarkan userId
    @GET("api/piket/{userId}")
    suspend fun getPiketUser(
        @Path("userId") userId: Int
    ): Response<List<PiketResponse>>

    // Create piket baru
    @POST("api/piket")
    suspend fun createPiket(
        @Body request: PiketRequest
    ): Response<PiketResponse>
}
