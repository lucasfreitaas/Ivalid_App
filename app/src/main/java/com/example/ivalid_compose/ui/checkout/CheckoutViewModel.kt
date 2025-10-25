package com.example.ivalid_compose.ui.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

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

class CheckoutViewModel : ViewModel(){
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
}