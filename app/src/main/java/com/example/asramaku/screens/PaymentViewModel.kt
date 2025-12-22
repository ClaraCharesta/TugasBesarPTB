package com.example.asramaku.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.asramaku.MyApp
import com.example.asramaku.data.model.Payment
import com.example.asramaku.data.remote.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class PaymentViewModel(
    private val api: ApiService
) : ViewModel() {


    private val _tagihanList = MutableStateFlow<List<Payment>>(emptyList())
    val tagihanList = _tagihanList.asStateFlow()


    private val _statusList = MutableStateFlow<List<Payment>>(emptyList())
    val statusList = _statusList.asStateFlow()


    private val _riwayatLunasList = MutableStateFlow<List<Payment>>(emptyList())
    val riwayatLunasList = _riwayatLunasList.asStateFlow()


    private val _detailPayment = MutableStateFlow<Payment?>(null)
    val detailPayment = _detailPayment.asStateFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _successMessage = MutableStateFlow("")
    val successMessage = _successMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()


    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "bukti_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { output ->
            inputStream?.copyTo(output)
        }
        return file
    }


    fun base64ToBitmap(base64: String?): Bitmap? {
        return try {
            if (base64.isNullOrEmpty()) return null
            val imageBytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            null
        }
    }


    fun loadTagihan(userId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getTagihan(userId)
                if (response.isSuccessful) {
                    _tagihanList.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error tagihan"
            }
        }
    }


    fun loadAllStatus(userId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getAllStatus(userId)
                if (response.isSuccessful) {
                    _statusList.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error status pembayaran"
            }
        }
    }


    fun loadRiwayatLunas(userId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getRiwayat(userId)
                if (response.isSuccessful) {
                    _riwayatLunasList.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error riwayat"
            }
        }
    }


    fun deletePayment(paymentId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                val response = api.deletePayment(paymentId)
                if (response.isSuccessful) {
                    loadRiwayatLunas(userId)
                    _successMessage.value = "Riwayat berhasil dihapus"
                } else {
                    _errorMessage.value = "Gagal menghapus riwayat"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error hapus data"
            }
        }
    }


    fun getDetailPayment(paymentId: Int) = liveData {
        try {
            val response = api.getPaymentDetail(paymentId)
            if (response.isSuccessful) {
                emit(response.body())
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }




    fun submitPayment(
        context: Context,
        userId: Int,
        bulan: String,
        totalTagihan: Int,
        buktiBayarUri: Uri?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userIdBody =
                    userId.toString().toRequestBody("text/plain".toMediaType())
                val bulanBody =
                    bulan.toRequestBody("text/plain".toMediaType())
                val totalBody =
                    totalTagihan.toString().toRequestBody("text/plain".toMediaType())

                val imagePart = buktiBayarUri?.let { uri ->
                    val file = uriToFile(context, uri)
                    val requestFile =
                        file.asRequestBody("image/*".toMediaType())

                    MultipartBody.Part.createFormData(
                        "buktiBayar", // HARUS SAMA DENGAN multer
                        file.name,
                        requestFile
                    )
                }

                val response = api.createPayment(
                    userIdBody,
                    bulanBody,
                    totalBody,
                    imagePart
                )

                if (response.isSuccessful) {
                    _successMessage.value =
                        response.body()?.message ?: "Pembayaran berhasil"
                } else {
                    _errorMessage.value = "Gagal mengirim pembayaran"
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error submit"
            }
            _isLoading.value = false
        }
    }
}
