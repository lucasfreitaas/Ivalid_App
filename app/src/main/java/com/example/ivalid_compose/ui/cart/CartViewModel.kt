package com.example.ivalid_compose.ui.cart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ivalid_compose.ui.home.Product

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val subtotal: Double get() = product.priceNow * quantity
}

data class CartUiState(
    val items: List<CartItem> = emptyList()
) {
    val total: Double get() = items.sumOf { it.subtotal }
    val count: Int get() = items.sumOf { it.quantity }
}

class CartViewModel : ViewModel() {

    var uiState by mutableStateOf(CartUiState())
        private set

    fun add(product: Product, quantity: Int) {
        require(quantity > 0)
        val existing = uiState.items.find { it.product.id == product.id }
        val newItems = if (existing == null) {
            uiState.items + CartItem(product, quantity)
        } else {
            uiState.items.map {
                if (it.product.id == product.id) it.copy(quantity = it.quantity + quantity) else it
            }
        }
        uiState = uiState.copy(items = newItems)
    }

    fun setQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            uiState = uiState.copy(items = uiState.items.filter { it.product.id != productId })
            return
        }
        uiState = uiState.copy(
            items = uiState.items.map {
                if (it.product.id == productId) it.copy(quantity = quantity) else it
            }
        )
    }

    fun remove(productId: String) {
        uiState = uiState.copy(items = uiState.items.filter { it.product.id != productId })
    }

    fun clear() {
        uiState = uiState.copy(items = emptyList())
    }
}