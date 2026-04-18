
package com.example.ivalid_compose.ui.orders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val isLoading: Boolean = true,
    val error: String? = null
)

class OrdersViewModel : ViewModel() {

    var uiState by mutableStateOf(OrdersUiState())
        private set

    // Criado uma única vez — evita recriar o objeto a cada parse de data
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            uiState = uiState.copy(isLoading = false, error = "Usuário não autenticado. Faça login novamente!")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            runCatching {
                FirebaseFirestore.getInstance()
                    .collection("pedidos")
                    .whereEqualTo("userId", userId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        runCatching {
                            val timestamp = doc.getTimestamp("timestamp")?.toDate() ?: Date()
                            @Suppress("UNCHECKED_CAST")
                            val itemsMaps = doc.get("itens") as? List<Map<String, Any>> ?: emptyList()
                            Order(
                                id = doc.id,
                                date = dateFormat.format(timestamp),
                                total = (doc.get("total") as? Number)?.toDouble() ?: 0.0,
                                status = doc.getString("status") ?: "Status desconhecido",
                                items = itemsMaps.map { m ->
                                    OrderItem(
                                        name = m["name"] as? String ?: "Item Desconhecido",
                                        quantity = (m["quantity"] as? Number)?.toInt() ?: 0,
                                        subtotal = (m["subtotal"] as? Number)?.toDouble() ?: 0.0
                                    )
                                }
                            )
                        }.getOrNull()
                    }
            }.onSuccess { orders ->
                uiState = uiState.copy(orders = orders, isLoading = false)
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, error = "Erro ao carregar pedidos: ${e.localizedMessage}")
            }
        }
    }
}