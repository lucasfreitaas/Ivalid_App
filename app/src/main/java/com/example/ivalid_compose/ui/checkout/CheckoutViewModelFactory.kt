package com.example.ivalid_compose.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ivalid_compose.ui.cart.CartViewModel

class CheckoutViewModelFactory(private val cartViewModel: CartViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CheckoutViewModel(cartViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}