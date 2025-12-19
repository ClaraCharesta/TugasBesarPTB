package com.example.asramaku.pembayaran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.asramaku.data.remote.ApiService
import com.example.asramaku.screens.PaymentViewModel

class PaymentViewModelFactory(
    private val api: ApiService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
