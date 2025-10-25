package com.example.ivalid_compose.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ivalid_compose.R
import kotlin.math.roundToInt

data class Category(
    val id: String,
    val name: String,
    val icon: Int? = null
)

data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val imageRes: Int,
    val storeName: String,
    val distanceKm: Double,
    val priceOriginal: Double,
    val priceNow: Double,
    val expiresInDays: Int,
    val categoryId: String,
    val isFavorite: Boolean = false
) {
    val discountPercent: Int
        get() = (((priceOriginal - priceNow) / priceOriginal) * 100).roundToInt().coerceAtLeast(0)
}

data class HomeUiState(
    val query: String = "",
    val selectedCategoryId: String? = null,
    val categories: List<Category> = emptyList(),
    val allProducts: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        loadInitial()
    }

    private fun loadInitial() {
        val cats = listOf(
            Category("all", "Tudo"),
            Category("enlatados", "Enlatados"),
            Category("padaria", "Padaria"),
            Category("frios", "Frios"),
            Category("bebidas", "Bebidas"),
            Category("congelados", "Congelados"),
        )

        val prods = listOf(
            Product(
                id = "1",
                name = "Milho Cozido 200g",
                brand = "Ivalid",
                imageRes = R.drawable.milho_lata,
                storeName = "Mercado Goiás",
                distanceKm = 1.2,
                priceOriginal = 5.30,
                priceNow = 2.25,
                expiresInDays = 2,
                categoryId = "enlatados"
            ),
            Product(
                id = "2",
                name = "Pão Francês 500g",
                brand = "Ivalid",
                imageRes = R.drawable.pao_frances,
                storeName = "Padaria Central",
                distanceKm = 0.8,
                priceOriginal = 8.49,
                priceNow = 4.99,
                expiresInDays = 1,
                categoryId = "padaria"
            ),
            Product(
                id = "3",
                name = "Presunto Suinco 200g",
                brand = "Suinco",
                imageRes = R.drawable.presunto,
                storeName = "Store",
                distanceKm = 2.5,
                priceOriginal = 10.50,
                priceNow = 8.99,
                expiresInDays = 5,
                categoryId = "frios"
            ),
            Product(
                id = "4",
                name = "Leite Integral 1L",
                brand = "Itambé",
                imageRes = R.drawable.leite,
                storeName = "Super Popular",
                distanceKm = 3.1,
                priceOriginal = 5.50,
                priceNow = 3.99,
                expiresInDays = 7,
                categoryId = "all"
            ),
            Product(
                id = "5",
                name = "Vinho Tinto 750ml",
                brand = "Pérgola",
                imageRes = R.drawable.vinho,
                storeName = "Assaí",
                distanceKm = 3.1,
                priceOriginal = 35.50,
                priceNow = 29.90,
                expiresInDays = 5,
                categoryId = "bebidas"
            )

        )

        uiState = HomeUiState(
            categories = cats,
            allProducts = prods
        )
        applyFilters()
    }

    fun onQueryChange(new: String) {
        uiState = uiState.copy(query = new)
        applyFilters()
    }

    fun onSelectCategory(id: String?) {
        uiState = uiState.copy(selectedCategoryId = id)
        applyFilters()
    }

    fun toggleFavorite(productId: String) {
        val updated = uiState.allProducts.map {
            if (it.id == productId) it.copy(isFavorite = !it.isFavorite) else it
        }
        uiState = uiState.copy(allProducts = updated)
        applyFilters()
    }

    private fun applyFilters() {
        val q = uiState.query.trim().lowercase()
        val cat = uiState.selectedCategoryId

        val filtered = uiState.allProducts.filter { p ->
            val matchesQuery = q.isEmpty() ||
                    p.name.lowercase().contains(q) ||
                    p.brand.lowercase().contains(q) ||
                    p.storeName.lowercase().contains(q)
            val matchesCat = (cat == null || cat == "all") || p.categoryId == cat
            matchesQuery && matchesCat
        }.sortedWith(
            compareBy<Product> { it.expiresInDays }
                .thenByDescending { it.discountPercent }
        )

        uiState = uiState.copy(filteredProducts = filtered)
    }
}