package com.example.ivalid_compose.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ivalid_compose.R
import com.example.ivalid_compose.ui.theme.AppTheme
import com.example.ivalid_compose.ui.theme.GreenAccent
import com.example.ivalid_compose.ui.theme.RedPrimary
import com.example.ivalid_compose.ui.theme.RedPrimaryDark
import com.example.ivalid_compose.ui.theme.YellowAccent

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Início")
    object Orders : BottomNavItem("orders", Icons.AutoMirrored.Filled.ListAlt, "Pedidos")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Perfil")
    object Settings : BottomNavItem("settings", Icons.Default.Settings, "Config.")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    userName: String, // recebe do pai, sem criar novo ViewModel aqui
    onOpenProduct: (Product) -> Unit,
    cartCount: Int = 0,
    navController: NavController
) {
    val state = viewModel.uiState
    var isMenuExpanded by remember { mutableStateOf(false) }

    // Criado uma única vez — não recriado a cada recomposição
    val sortOptions = remember {
        listOf(
            "Padrão (Vencimento)" to ProductSortOption.DEFAULT,
            null to ProductSortOption.DEFAULT, // divider
            "Menor Preço" to ProductSortOption.PRICE_ASC,
            "Maior Preço" to ProductSortOption.PRICE_DESC,
            null to ProductSortOption.DEFAULT, // divider
            "Maior Desconto" to ProductSortOption.DISCOUNT_DESC,
            "Menor Desconto" to ProductSortOption.DISCOUNT_ASC,
            null to ProductSortOption.DEFAULT, // divider
            "Mais Próximo (km)" to ProductSortOption.DISTANCE_ASC,
            "Mais Distante (km)" to ProductSortOption.DISTANCE_DESC
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width(4.dp))
                        Image(
                            painter = painterResource(id = R.drawable.logo_ivalid),
                            contentDescription = "Logo Ivalid",
                            modifier = Modifier.size(24.dp).clip(CircleShape),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(Modifier.width(8.dp))

                        Column {
                            Text(
                                userName.split(" ").firstOrNull() ?: "Cliente",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "Ofertas perto de você",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
                                Badge { Text(cartCount.coerceAtMost(99).toString()) }
                            }
                        }
                    ) {
                        IconButton(onClick = { navController.navigate("cart") }) {
                            Icon(imageVector = Icons.Outlined.ShoppingCart, contentDescription = "Carrinho")
                        }
                    }
                    IconButton(onClick = { /* Navegação para notificações */ }) {
                        Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "Notificações")
                    }
                },
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End){
                FloatingActionButton(onClick = { isMenuExpanded = true}, containerColor = RedPrimary){
                    Icon(Icons.Filled.Sort, contentDescription = "Ordenar por: ", tint = Color.White)
                }

                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = {isMenuExpanded = false},
                    modifier = Modifier.width(200.dp)
                ) {
                    sortOptions.forEach { (label, option) ->
                        if (label == null) {
                            Divider()
                        } else {
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    isMenuExpanded = false
                                    viewModel.sortProducts(option)
                                },
                                leadingIcon = {
                                    if (state.currentSort == option) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    SearchBar(
                        query = state.query,
                        onQueryChange = viewModel::onQueryChange,
                        onClear = { viewModel.onQueryChange("") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    CategoryChips(
                        categories = state.categories,
                        selectedId = state.selectedCategoryId ?: "all",
                        onSelect = { id -> viewModel.onSelectCategory(if (id == "all") null else id) }
                    )
                    Spacer(Modifier.height(16.dp))
                    OfferBanner(modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Ofertas perto do vencimento",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
                items(
                    items = state.filteredProducts,
                    // Usamos o ID do produto para o Compose saber que cada item é único
                    key = { product -> product.id.ifEmpty { "${product.name}_${product.priceNow}" } }
                ) { product -> // O lambda do conteúdo deve abrir aqui
                    ProductCard(
                        product = product,
                        onClick = { onOpenProduct(product) },
                        onToggleFavorite = { viewModel.toggleFavorite(product.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
            }
        }
    }
}
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClick: () -> Unit = {},
    onClear: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar produto, marca ou loja") },
        leadingIcon = {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_search),
                contentDescription = null,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                TextButton(onClick = onClear) { Text("Limpar") }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),

        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
            cursorColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    )
}

@Composable
private fun CategoryChips(
    categories: List<Category>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(categories.size, key = { it }){ idx ->
            val c = categories[idx]
            val selected = (selectedId == c.id)
            FilterChip(
                selected = selected,
                onClick = { onSelect(c.id) },
                label = { Text(c.name) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = if (selected) RedPrimary.copy(alpha = 0.12f) else Color(0xFFF2F2F2),
                    labelColor = if (selected) RedPrimary else MaterialTheme.colorScheme.onSurface,
                    selectedContainerColor = RedPrimary.copy(alpha = 0.20f),
                    selectedLabelColor = RedPrimary,
                    selectedLeadingIconColor = RedPrimary
                )
            )
        }
    }
}

@Composable
private fun OfferBanner(
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(RedPrimary, RedPrimaryDark)
                )
            )
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = "Aproveite descontos de até 70%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(28.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(GreenAccent)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Itens próximos da validade • Estoque limitado",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                )
            }
        }
    }
}
@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    Card(
        onClick = onClick,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .shadow(elevation = 0.dp, shape = shape)
            .height(310.dp)
    ) {
        Column(Modifier.padding(10.dp).fillMaxSize()) {

            Box(
                modifier = Modifier
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                key(product.id){
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.urlImagem)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxWidth().height(110.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(RedPrimary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "-${product.discountPercent}%",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (product.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = if (product.isFavorite) RedPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val (bg, fg) = when {
                    product.expiresInDays <= 10 -> RedPrimary.copy(alpha = 0.15f) to RedPrimary
                    product.expiresInDays <= 30 -> YellowAccent.copy(alpha = 0.18f) to YellowAccent
                    else -> GreenAccent.copy(alpha = 0.16f) to GreenAccent
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(bg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Vence em ${product.expiresInDays}d",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = fg,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                product.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                "${product.storeName} • ${"%.1f".format(product.distanceKm)} km",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "R$ ${"%.2f".format(product.priceNow)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = RedPrimary
                    )
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "R$ ${"%.2f".format(product.priceOriginal)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough
                    )
                )
            }

            Spacer(Modifier.weight(1f))

            GradientRedButton(
                text = "Ver oferta",
                enabled = true,
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GradientRedButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val gradient = Brush.horizontalGradient(colors = listOf(RedPrimary, RedPrimaryDark))
    val alpha = if (enabled) 1f else 0.5f
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(shape)
            .background(brush = gradient, alpha = alpha)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun PreviewHomeLight() {
    AppTheme(darkTheme = false) {
        HomeScreen(
            viewModel = HomeViewModel(),
            userName = "Preview",
            onOpenProduct = {},
            navController = rememberNavController()
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun PreviewHomeDark() {
    AppTheme(darkTheme = true) {
        HomeScreen(
            viewModel = HomeViewModel(),
            userName = "Preview",
            onOpenProduct = {},
            navController = rememberNavController()
        )
    }
}
