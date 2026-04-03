package com.example.ivalid_compose.ui.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.example.ivalid_compose.ui.cart.CartViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class Cartao(
    val id: String,
    val numero: String,
    val finalNumero: String,
    val validade: String
)

data class Endereco(
    val rua: String = "Rua 67-A",
    val bairro: String = "St. Centro - 658"
)

data class CheckoutUiState(
    val endereco: Endereco = Endereco(),
    val opcaoEntrega: String = "Padrão",
    val valorEntrega: String = "Grátis",
    val formaPagamentoSelecionada: String = "Pix",
    val cartoesSalvos: List<Cartao> = emptyList(),
    val isDailogAddCardVisible: Boolean = false,
    val isFinalizing: Boolean = false,
    val isDetalhesExpandido: Boolean = false,
    val pixGerado: String? = null,
    val apiError: String? = null
)

class CheckoutViewModel(private val cartViewModel: CartViewModel): ViewModel(){
    var uiState by mutableStateOf(CheckoutUiState())
        private set

    private val _orderPlacedEvent = MutableSharedFlow<Pair<String, Double>>()
    val orderPlacedEvent = _orderPlacedEvent.asSharedFlow()
    fun toggleDetalhes(){
        uiState = uiState.copy(isDetalhesExpandido = !uiState.isDetalhesExpandido,)
    }

    fun trocarEndereco(){

    }

    fun setFormaPagamento(forma: String) {
        uiState = uiState.copy(formaPagamentoSelecionada = forma)
    }

    fun toggleDialogAddCard(visible: Boolean) {
        uiState = uiState.copy(isDailogAddCardVisible = visible, apiError = null)
    }

    fun adicionarCartao(numero: String, validade: String, cvv: String): Boolean {
        // Validação básica do cartão
        if (numero.replace(" ", "").length !in 13..19) {
            uiState = uiState.copy(apiError = "Número do cartão inválido")
            return false
        }
        if (!validade.matches(Regex("(0[1-9]|1[0-2])/[0-9]{2}"))) {
            uiState = uiState.copy(apiError = "Validade inválida. Use MM/AA")
            return false
        }
        if (cvv.length !in 3..4) {
            uiState = uiState.copy(apiError = "Código de segurança inválido")
            return false
        }

        val novoCartao = Cartao(
            id = System.currentTimeMillis().toString(),
            numero = numero,
            finalNumero = numero.takeLast(4),
            validade = validade
        )
        uiState = uiState.copy(
            cartoesSalvos = uiState.cartoesSalvos + novoCartao,
            isDailogAddCardVisible = false,
            apiError = null
        )
        return true
    }
    fun finalizarPedido() {
        if (uiState.isFinalizing) return

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null){
            uiState = uiState.copy(apiError = "Usuário não autenticado. Faça login novamente.")
            return
        }

        uiState = uiState.copy(isFinalizing = true, apiError = null)

        viewModelScope.launch {
            val pedido = hashMapOf(
                "userId" to user.uid,
                "endereco" to uiState.endereco.rua + ", " + uiState.endereco.bairro,
                "total" to cartViewModel.uiState.total,
                "formaPagamento" to uiState.formaPagamentoSelecionada,
                "status" to if (uiState.formaPagamentoSelecionada == "Pix") "Pagamento pendente" else "Preparando pedido",
                "itens" to cartViewModel.uiState.items.map { item ->
                    hashMapOf(
                        "productId" to item.product.id,
                        "name" to item.product.name,
                        "quantity" to item.quantity,
                        "subtotal" to item.subtotal
                    )
                },
                "timestamp" to Timestamp.now()
            )

            FirebaseFirestore.getInstance().collection("pedidos")
                .add(pedido)
                .addOnSuccessListener { documentReference ->
                    uiState = uiState.copy(isFinalizing = false)
                    val orderId = documentReference.id
                    val finalTotal = cartViewModel.uiState.total

                    viewModelScope.launch {
                        _orderPlacedEvent.emit(Pair(orderId, finalTotal))
                    }

                    cartViewModel.clear()
                }
                .addOnFailureListener { e ->
                    uiState = uiState.copy(
                        isFinalizing = false,
                        apiError = "Falha ao registrar pedido."
                    )
                }
        }
    }
}