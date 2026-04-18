package com.example.ivalid_compose.ui.donation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ivalid_compose.domain.donation.DonationGamificationService

data class DonationItem(
    val id: String,
    val name: String,
    val ngoName: String,
    val price: Double,
    val imageUrl: String
)

data class DonationUiState(
    val explainerDialogVisible: Boolean = false,
    val donations: List<DonationItem> = listOf(
        DonationItem("1", "Cesta Básica Completa", "ONG Ação da Cidadania", 80.00, "https://via.placeholder.com/150"),
        DonationItem("2", "Kit Higiene", "ONG SP Solidária", 35.50, "https://via.placeholder.com/150"),
        DonationItem("3", "Fraldas Infantis", "Casa da Criança", 45.00, "https://via.placeholder.com/150")
    ),
    val cartCount: Int = 0 // Simula itens de doação no carrinho
)

class DonationViewModel : ViewModel() {
    var uiState by mutableStateOf(DonationUiState())
        private set

    private val service = DonationGamificationService()

    fun dismissExplainerDialog() {
        uiState = uiState.copy(explainerDialogVisible = false)
    }

    fun showExplainerDialog() {
        uiState = uiState.copy(explainerDialogVisible = true)
    }

    // Ação fictícia para adicionar à sacola de doação
    fun addToDonationCart(item: DonationItem) {
        uiState = uiState.copy(cartCount = uiState.cartCount + 1)
    }
}
