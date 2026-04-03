package com.example.ivalid_compose.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ivalid_compose.ui.cart.CartViewModel
import com.example.ivalid_compose.ui.home.Product
import com.example.ivalid_compose.ui.theme.GreenAccent
import com.example.ivalid_compose.ui.theme.GreenAccentDark
import com.example.ivalid_compose.ui.theme.RedPrimary
import com.example.ivalid_compose.ui.theme.RedPrimaryDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: Product?,
    onBack: () -> Unit,
    onAddedToCart: () -> Unit,
    cartViewModel: CartViewModel
) {
    println("DETALHES: Carregando produto ${product?.name} com imagem ${product?.urlImagem}")
    val snackbarHostState = remember { SnackbarHostState() }

    if (product == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalhes") },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Voltar") }
                    }
                )
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Produto não encontrado", color = MaterialTheme.colorScheme.error)
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        var quantity by remember { mutableStateOf(1) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                coil.compose.AsyncImage(
                    model = product.urlImagem,
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    error = coil.compose.rememberAsyncImagePainter(model = null),
                    placeholder = coil.compose.rememberAsyncImagePainter(model = null)
                )

                val (bg, fg) = when {
                    product.expiresInDays <= 10 -> RedPrimary.copy(alpha = 0.15f) to RedPrimary
                    product.expiresInDays <= 30 -> Color(0xFFE9C46A).copy(alpha = 0.20f) to Color(0xFFD4A017)
                    else -> GreenAccent.copy(alpha = 0.15f) to GreenAccentDark
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                   Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(bg)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Vence em ${product.expiresInDays}d",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = fg, fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(RedPrimary)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "-${product.discountPercent}%",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    product.brand.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Storefront, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        product.storeName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${"%.1f".format(product.distanceKm)} km",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Divider(Modifier.padding(vertical = 20.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                Text(
                    "Preço Especial",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "R$ ${"%.2f".format(product.priceNow)}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "R$ ${"%.2f".format(product.priceOriginal)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        ),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Spacer(Modifier.height(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text("Quantidade", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    QuantityStepper(
                        value = quantity,
                        onIncrement = { quantity += 1 },
                        onDecrement = { if (quantity > 1) quantity -= 1 }
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GreenAccent.copy(alpha=0.1f))
                        .padding(16.dp)
                ) {
                   Icon(Icons.Outlined.Info, contentDescription = null, tint = GreenAccentDark)
                   Spacer(Modifier.width(12.dp))
                   Text(
                       "Retirada até ${calcPickupDeadline(product.expiresInDays)}.",
                       style = MaterialTheme.typography.bodyMedium,
                       color = GreenAccentDark
                   )
                }

                Spacer(Modifier.height(32.dp))

                val scope = rememberCoroutineScope()

                GradientRedButton(
                    text = "Adicionar ao carrinho",
                    enabled = quantity > 0,
                    onClick = {
                        cartViewModel.add(product, quantity)
                        scope.launch { snackbarHostState.showSnackbar("Adicionado ao carrinho") }
                        onAddedToCart()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun QuantityStepper(
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onDecrement, enabled = value > 1) {
            Icon(Icons.Outlined.Remove, contentDescription = "Diminuir")
        }
        Text(
            value.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.widthIn(min = 32.dp),
        )
        IconButton(onClick = onIncrement) {
            Icon(Icons.Outlined.Add, contentDescription = "Aumentar")
        }
    }
}

private fun calcPickupDeadline(expiresInDays: Int): String {
    return when {
        expiresInDays <= 0 -> "hoje"
        expiresInDays == 1 -> "amanhã"
        else -> "em até $expiresInDays dias"
    }
}

@Composable
private fun GradientRedButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val gradient = Brush.horizontalGradient(listOf(RedPrimary, RedPrimaryDark))
    val contentAlpha = if (enabled) 1f else 0.5f

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(shape)
            .background(brush = gradient)
            .alpha(contentAlpha)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}