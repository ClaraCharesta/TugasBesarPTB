package com.example.asramaku.data.remote

import com.example.asramaku.data.model.LoginRequest
import com.example.asramaku.data.model.LoginResponse
import com.example.asramaku.data.model.RegisterRequest
import com.example.asramaku.data.model.PiketRequest
import com.example.asramaku.data.model.PiketResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Part

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

    // Create report (temanmu)
    @Multipart
    @POST("api/reports")
    suspend fun createReport(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("location") location: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Response<ResponseBody>
}
