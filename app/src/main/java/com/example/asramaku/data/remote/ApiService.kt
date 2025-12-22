package com.example.asramaku.data.remote

import com.example.asramaku.data.model.*
import com.example.asramaku.piket.RiwayatPiket
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {


    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<LoginResponse>


    @GET("api/piket/{userId}")
    suspend fun getPiketUser(
        @Path("userId") userId: Int
    ): Response<List<PiketResponse>>

    @POST("api/piket")
    suspend fun createPiket(
        @Body request: PiketRequest
    ): Response<PiketResponse>


    @PUT("api/piket/update-tanggal")
    suspend fun updateTanggalPiket(
        @Body body: Map<String, String>
    ): Response<Void>


    @GET("api/piket/riwayat/{userId}")
    suspend fun getRiwayatPiket(
        @Path("userId") userId: Int
    ): List<RiwayatPiket>


    @Multipart
    @POST("api/piket/selesai")
    suspend fun selesaikanPiket(
        @Part("jadwalId") jadwalId: RequestBody,
        @Part foto: MultipartBody.Part
    ): Response<SelesaiPiketResponse>


    @Multipart
    @POST("api/reports")
    suspend fun createReport(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("location") location: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Response<ResponseBody>

    @GET("api/reports")
    suspend fun getMyReports(
        @Header("Authorization") token: String
    ): Response<ReportListResponse>

    @GET("api/reports/{id}")
    suspend fun getReportById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ReportDetailResponse>

    @DELETE("api/reports/{id}")
    suspend fun deleteReport(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @Multipart
    @PUT("api/reports/{id}")
    suspend fun updateReport(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("location") location: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Response<ReportDetailResponse>


    @GET("api/payments/tagihan/{userId}")
    suspend fun getTagihan(
        @Path("userId") userId: Int
    ): Response<TagihanResponse>


    @Multipart
    @POST("api/payments/create")
    suspend fun createPayment(
        @Part("userId") userId: RequestBody,
        @Part("bulan") bulan: RequestBody,
        @Part("totalTagihan") totalTagihan: RequestBody,
        @Part buktiBayar: MultipartBody.Part?   // optional
    ): Response<PaymentCreateResponse>


    @GET("api/payments/riwayat/{userId}")
    suspend fun getRiwayat(
        @Path("userId") userId: Int
    ): Response<List<Payment>>


    @GET("api/payments/status/{userId}")
    suspend fun getAllStatus(
        @Path("userId") userId: Int
    ): Response<List<Payment>>


    @GET("api/payments/detail/{id}")
    suspend fun getPaymentDetail(
        @Path("id") paymentId: Int
    ): Response<DetailPembayaranResponse>



    @DELETE("api/payments/{id}")
    suspend fun deletePayment(
        @Path("id") paymentId: Int
    ): Response<MessageResponse>
}


data class TagihanResponse(
    val success: Boolean,
    val data: List<Payment>
)

data class PaymentCreateResponse(
    val message: String,
    val data: Payment
)

data class MessageResponse(
    val message: String
)
