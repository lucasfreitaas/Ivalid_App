package com.example.ivalid_compose.ui.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ivalid_compose.ui.theme.AppTheme
import com.example.ivalid_compose.ui.theme.GreenAccent
import com.example.ivalid_compose.ui.theme.RedPrimary
import com.example.ivalid_compose.ui.theme.RedPrimaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = viewModel(),
    onOpenOrderDetails: (String) -> Unit
) {
    val state = viewModel.uiState

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Meus Pedidos", color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = RedPrimary)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        ) {
            if (state.orders.isEmpty()) item { EmptyOrdersMessage() }
            else items(state.orders) { order ->
                OrderCard(order = order, onOpen = onOpenOrderDetails)
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order, onOpen: (String) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    val (statusColor, statusIcon) = when (order.status) {
        "Pagamento pix pendente" -> RedPrimary to Icons.Default.CalendarToday
        "Em preparação" -> GreenAccent to Icons.Default.ShoppingBag
        "Entregue" -> Color(0xFF4CAF50) to Icons.AutoMirrored.Filled.ArrowForward
        else -> Color.Gray to Icons.Default.ShoppingBag
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { /* onOpen(order.id) */ }),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End // Alinhamento à direita para o status
            ) {
                Surface(
                    color = statusColor.copy(0.12f),
                    contentColor = statusColor,
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = statusIcon, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            order.status,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                "ID: #${order.id.take(8)}...",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            Spacer(Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Data:", color = Color.Gray)
                    Text(order.date, fontWeight = FontWeight.Medium)
                }
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Total:", fontWeight = FontWeight.SemiBold, color = RedPrimary)
                    Text("R$ %.2f".format(order.total), fontWeight = FontWeight.Bold, color = RedPrimary)
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isExpanded = !isExpanded }
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Itens no Pedido (${order.items.size})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {

                    Spacer(Modifier.height(12.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        onClick = { onOpen(order.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimaryDark)
                    ) {
                        Text("Ver Detalhes do Pedido")
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyOrdersMessage() {
    Column(
        modifier = Modifier.fillMaxWidth().height(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = null,
            modifier = Modifier.size(70.dp),
            tint = Color.LightGray
        )
        Spacer(Modifier.height(14.dp))
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
    AppTheme {
        OrdersScreen(
            viewModel = viewModel(),
            onOpenOrderDetails = {})
    }
}