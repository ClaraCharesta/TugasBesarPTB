package com.example.asramaku.data.remote

import com.example.asramaku.data.model.LoginRequest
import com.example.asramaku.data.model.LoginResponse
import com.example.asramaku.data.model.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<LoginResponse>

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
