package com.example.ivalid_compose.ui.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.example.ivalid_compose.ui.cart.CartViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


data class Endereco(
    val rua: String = "Rua 67-A",
    val bairro: String = "St. Centro - 658"
)

data class CheckoutUiState(
    val endereco: Endereco = Endereco(),
    val opcaoEntrega: String = "Padrão",
    val valorEntrega: String = "Grátis",
    val formaPagamentoSelecionada: String = "Pix",
    val isFinalizing: Boolean = false,
    val isDetalhesExpandido: Boolean = false
)

class CheckoutViewModel(private val cartViewModel: CartViewModel): ViewModel(){
    var uiState by mutableStateOf(CheckoutUiState())
        private set

    fun finalizarPedido(){
        uiState = uiState.copy(isFinalizing = true)
    }

    fun toggleDetalhes(){
        uiState = uiState.copy(isDetalhesExpandido = !uiState.isDetalhesExpandido)
    }

    fun trocarEndereco(){

    }

    private val _orderPlacedEvent = MutableSharedFlow<String>()
    val orderPlacedEvent = _orderPlacedEvent.asSharedFlow()

    fun finalizarPedido(
        onSuccess: (String) -> Unit,
        onErrror: (String) -> Unit
    ) {
        if(uiState.isFinalizing) return

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null){
            onErrror("Usuário não autenticado.")
            return
        }

        uiState = uiState.copy(isFinalizing = true)

        val pedido = hashMapOf(
            "userId" to user.uid,
            "endereco" to uiState.endereco.rua + ", " + uiState.endereco.bairro,
            "total" to cartViewModel.uiState.total,
            "formaPagamento" to uiState.formaPagamentoSelecionada,
            "status" to "Pagamento pix pendente",
            "itens" to cartViewModel.uiState.items.map { item ->
                hashMapOf(
                    "productId" to item.product.id,
                    "name" to item.product.name,
                    "quantity" to item.quantity,
                    "subtotal" to item.subtotal
                )
            },
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        FirebaseFirestore.getInstance().collection("pedidos")
            .add(pedido)
            .addOnSuccessListener { documentReference ->
                uiState = uiState.copy(isFinalizing = false)
                val orderId = documentReference.id

                viewModelScope.launch {
                    _orderPlacedEvent.emit(orderId)
                }

                cartViewModel.clear()
            }

            .addOnFailureListener { e ->
                uiState = uiState.copy(isFinalizing = false)
                onErrror("Falha ao registrar pedido: ${e.message }")
            }
    }
}