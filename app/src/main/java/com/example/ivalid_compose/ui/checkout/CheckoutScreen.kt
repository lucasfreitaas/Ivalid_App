package com.example.ivalid_compose.ui.checkout

import android.R.attr.navigationIcon
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ivalid_compose.ui.theme.AppTheme
import com.example.ivalid_compose.ui.theme.GreenAccent
import com.example.ivalid_compose.ui.theme.RedPrimary
import com.example.ivalid_compose.R
import com.example.ivalid_compose.ui.cart.CartViewModel

// Cores utilizadas no modelo do seu app (simulação)
private val HeaderColor = Color(0xFFF0FFF0)
private val SelectedDotColor = RedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel,
    cartViewModel: CartViewModel,
    onFinalizarPedido: () -> Unit,
    onTrocarEndereco: () -> Unit,
    onBack: () -> Unit
    // totalDoCarrinho: String // Você pode passar o total ou o VM do carrinho
) {
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrinho") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RedPrimary,
                    titleContentColor = Color.White
                ),

            navigationIcon = {
                IconButton(onClick = onBack){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.Black
                        )
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = onFinalizarPedido,
                enabled = !state.isFinalizing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (state.isFinalizing) "Finalizando..." else "Finalizar pedido",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            EntregaHeader(
                endereco = state.endereco,
                onTrocarEndereco = onTrocarEndereco
            )

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            OpcoesEntrega(state)

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            FormasPagamento(state)

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            DetalhesPedidoExpansivel(
                viewModel = viewModel,
                cartViewModel = cartViewModel
            )

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        }
    }
}
@Composable
fun EntregaHeader(endereco: Endereco, onTrocarEndereco: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Entregar no endereço",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = "Localização", tint = RedPrimary)
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(endereco.rua, style = MaterialTheme.typography.bodyLarge)
                Text(endereco.bairro, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text(
                "Trocar",
                color = RedPrimary,
                modifier = Modifier.clickable(onClick = onTrocarEndereco)
            )
        }
    }
}

@Composable
fun OpcoesEntrega(state: CheckoutUiState) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Opções de entrega",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(state.opcaoEntrega, style = MaterialTheme.typography.bodyLarge)
                Text("Hoje, 10 - 20 min", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text(
                state.valorEntrega,
                color = GreenAccent,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(16.dp))
            RedDot(isSelected = true)
        }
    }
}

// Sub-Composable para Formas de Pagamento
@Composable
fun FormasPagamento(state: CheckoutUiState) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Formas de pagamento",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(Modifier.height(12.dp))

        // Opção PIX (Sempre Selecionada)
        PagamentoItem(
            icone = { Icon(painterResource(R.drawable.pix), contentDescription = "Pix", modifier = Modifier.size(24.dp)) }, // Assumindo que você tem um ícone Pix
            texto = "Pix",
            isSelected = state.formaPagamentoSelecionada == "Pix"
        )

        // Opção Cartão (Desmarcada)
        PagamentoItem(
            icone = { Icon(Icons.Filled.Warning, contentDescription = "Cartão", modifier = Modifier.size(24.dp)) }, // Use um ícone real de cartão
            texto = "Cartão - (Debito ou Credito)",
            isSelected = state.formaPagamentoSelecionada == "Cartão"
        )

        // Opção Dinheiro (Desmarcada)
        PagamentoItem(
            icone = { Icon(Icons.Filled.Warning, contentDescription = "Dinheiro", modifier = Modifier.size(24.dp)) }, // Use um ícone real de dinheiro
            texto = "Dinheiro",
            isSelected = state.formaPagamentoSelecionada == "Dinheiro"
        )
    }
}

@Composable
fun DetalhesPedidoExpansivel(
    viewModel: CheckoutViewModel,
    cartViewModel: CartViewModel
) {
    val checkoutState = viewModel.uiState
    val cartState = cartViewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{viewModel.toggleDetalhes()}
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Detalhes do pedido",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Icon(
                imageVector = if (checkoutState.isDetalhesExpandido) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if(checkoutState.isDetalhesExpandido) "Recolher" else "Expandir",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        AnimatedVisibility(visible = checkoutState.isDetalhesExpandido) {
            Column(modifier = Modifier.padding(top = 12.dp)){
                if (cartState.items.isEmpty()){
                    Text("Nenhum item no carrinho.", color = Color.Gray)
                } else {
                    cartState.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity} x ${item.product.name}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text (
                                text = "R$ %.2f".format(item.subtotal),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(
                            text = "R$ %.2f".format(cartState.total),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = RedPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PagamentoItem(icone: @Composable () -> Unit, texto: String, isSelected: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icone()
        Spacer(Modifier.width(12.dp))
        Text(texto, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        RedDot(isSelected = isSelected)
    }
}

@Composable
fun RedDot(isSelected: Boolean) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .border(
                width = 2.dp,
                color = SelectedDotColor,
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(SelectedDotColor)
            )
        }
    }
}

// Pré-visualização da Tela de Checkout
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "Checkout View")
@Composable
private fun PreviewCheckoutScreen() {

    AppTheme {
        CheckoutScreen(
            viewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        CheckoutViewModel()
                    }
                }
            ),
            onFinalizarPedido = {},
            onTrocarEndereco = {},
            onBack = {},
            cartViewModel = CartViewModel()
        )
    }
}