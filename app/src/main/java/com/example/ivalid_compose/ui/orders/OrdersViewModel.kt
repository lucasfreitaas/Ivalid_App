package com.example.ivalid_compose.ui.orders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ivalid_compose.R
import com.example.ivalid_compose.ui.home.Product

data class OrderItem(
    val name: String,
    val quantity: Int,
    val subtotal: Double
)

data class Order(
    val id: String,
    val date: String,
    val total: Double,
    val status: String,
    val items: List<OrderItem>
)

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class OrdersViewModel: ViewModel(){
    var uiState by mutableStateOf(OrdersUiState(orders = mockOrders))
        private set
}

private val mockProducts = listOf(
    Product(
        id = "p1",
        name = "Pão Francês 500g",
        priceNow = 4.99,
        priceOriginal = 8.49,
        expiresInDays = 1,
        isFavorite = false,
        imageRes = R.drawable.logo_ivalid, // Substitua pelo R.drawable real do pão
        distanceKm = 0.8,
        storeName = "Padaria Central",
        brand = "Visconti",
        categoryId = "Padaria"
    ),
    Product(
        id = "p2",
        name = "Milho Cozido 200g",
        priceNow = 2.25,
        priceOriginal = 5.30,
        expiresInDays = 3,
        isFavorite = false,
        imageRes = R.drawable.logo_ivalid,
        distanceKm = 1.2,
        storeName = "Mercado Goiás",
        brand = "Quero",
        categoryId = "Enlatados"
    ),
    Product(
        id = "p3",
        name = "Vinho Tinto 750ml",
        priceNow = 29.90,
        priceOriginal = 35.50,
        expiresInDays = 5,
        isFavorite = true,
        imageRes = R.drawable.logo_ivalid, // Substitua pelo R.drawable real do vinho
        distanceKm = 3.1,
        storeName = "Assaí",
        brand = "Pérgola",
        categoryId = "Bebidas"
    )
)

private val mockOrders = listOf(
    Order(
        id = "ABC12345",
        date = "25/10/2025",
        total = 75.50,
        status = "Pagamento Pendente",
        items = listOf(
            OrderItem(mockProducts[0].name, 5, 24.95),
            OrderItem(mockProducts[1].name, 2, 4.50),
            OrderItem(mockProducts[2].name, 1, 46.05)
        )
    ),
)