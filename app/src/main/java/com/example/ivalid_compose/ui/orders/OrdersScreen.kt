package com.example.ivalid_compose.ui.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ivalid_compose.ui.theme.AppTheme
import com.example.ivalid_compose.ui.theme.GreenAccent
import com.example.ivalid_compose.ui.theme.RedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = viewModel(),
    onOpenOrderDetails: (String) -> Unit // Para navegar para detalhes de um pedido específico
) {
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Pedidos", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RedPrimary)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp), // Padding Bottom para a BottomBar
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.orders.isEmpty() && !state.isLoading) {
                item { EmptyOrdersMessage() }
            } else {
                items(state.orders) { order ->
                    OrderCard(order = order, onClick = { onOpenOrderDetails(order.id) })
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, onClick: () -> Unit) {
    // Estado local para controlar se os detalhes do item estão visíveis dentro do Card
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // --- Cabeçalho do Pedido (ID e Status) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pedido #${order.id}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                // Mapeamento de cor do Status
                val statusColor = when (order.status) {
                    "Pagamento Pendente" -> RedPrimary
                    "Em Preparação" -> GreenAccent
                    "Entregue" -> Color(0xFF4CAF50) // Verde Escuro
                    else -> Color.Gray
                }
                Text(order.status, style = MaterialTheme.typography.bodySmall, color = statusColor)
            }

            Spacer(Modifier.height(8.dp))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(Modifier.height(8.dp))

            // --- Detalhes Principais (Data e Total) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Data:", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text(order.date, style = MaterialTheme.typography.bodyMedium)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Text("R$ %.2f".format(order.total), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            }

            Spacer(Modifier.height(12.dp))

            // --- Botão Expansível para Itens do Pedido ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isExpanded = !isExpanded }
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ver Itens (${order.items.size})", style = MaterialTheme.typography.labelLarge)
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = "Expandir Itens",
                    modifier = Modifier.size(20.dp)
                )
            }

            // --- Conteúdo Expansível (Lista de Itens) ---
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.quantity}x ${item.name}", style = MaterialTheme.typography.bodySmall)
                            Text("R$ %.2f".format(item.subtotal), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyOrdersMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = "Nenhum Pedido",
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Você ainda não fez nenhum pedido.",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewOrdersScreen() {
    val ordersViewModel = viewModel<OrdersViewModel>()
    AppTheme {
        OrdersScreen(
            viewModel = ordersViewModel,
            onOpenOrderDetails = {}
            )
    }
}