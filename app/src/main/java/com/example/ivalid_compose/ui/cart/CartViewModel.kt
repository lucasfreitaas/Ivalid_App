package com.example.ivalid_compose.ui.cart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ivalid_compose.domain.donation.DonationGamificationService
import com.example.ivalid_compose.ui.home.Product

enum class OriginType {
    VENDA_DIRETA,
    DOACAO
}

data class CartItem(
    val product: Product,
    val quantity: Int,
    val origin: OriginType = OriginType.VENDA_DIRETA
) {
    val subtotal: Double get() = product.priceNow * quantity
}

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val userTotalDonationsMock: Int = 15 // Mock of user level 'Prata' (3%)
) {
    val total: Double get() = items.sumOf { it.subtotal }
    val count: Int get() = items.sumOf { it.quantity }
    val donationSubtotal: Double get() = items.filter { it.origin == OriginType.DOACAO }.sumOf { it.subtotal }
}

class CartViewModel : ViewModel() {

    var uiState by mutableStateOf(CartUiState())
        private set

    val gamificationService = DonationGamificationService()

    fun add(product: Product, quantity: Int, isDonationContext: Boolean = false) {
        require(quantity > 0)
        
        val originType = if (isDonationContext) OriginType.DOACAO else OriginType.VENDA_DIRETA
        
        // Items are grouped by ID AND their origin type (can't mix donation with direct sale in the same row)
        val existing = uiState.items.find { it.product.id == product.id && it.origin == originType }
        val newItems = if (existing == null) {
            uiState.items + CartItem(product, quantity, originType)
        } else {
            uiState.items.map {
                if (it.product.id == product.id && it.origin == originType) it.copy(quantity = it.quantity + quantity) else it
            }
        }
        uiState = uiState.copy(items = newItems)
    }

    fun setQuantity(productId: String, quantity: Int, originType: OriginType = OriginType.VENDA_DIRETA) {
        if (quantity <= 0) {
            uiState = uiState.copy(items = uiState.items.filterNot { it.product.id == productId && it.origin == originType })
            return
        }
        uiState = uiState.copy(
            items = uiState.items.map {
                if (it.product.id == productId && it.origin == originType) it.copy(quantity = quantity) else it
            }
        )
    }

    fun remove(productId: String, originType: OriginType = OriginType.VENDA_DIRETA) {
        uiState = uiState.copy(items = uiState.items.filterNot { it.product.id == productId && it.origin == originType })
    }

    fun clear() {
        uiState = uiState.copy(items = emptyList())
    }
}