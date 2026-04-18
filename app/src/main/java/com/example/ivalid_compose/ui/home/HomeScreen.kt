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
    object Donation : BottomNavItem("donation", Icons.Outlined.FavoriteBorder, "Doação")
    object Offers : BottomNavItem("offers", Icons.Outlined.LocalOffer, "Descontão")
    object Orders : BottomNavItem("orders", Icons.AutoMirrored.Filled.ListAlt, "Pedidos")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Perfil")
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
    val shape = RoundedCornerShape(20.dp)
    
    // Central visual focus: Urgência do Vencimento
    val urgencyBgColor = when {
        product.expiresInDays <= 10 -> Color(0xFFFFEBEE)   // 0-10 dias: Vermelho
        product.expiresInDays <= 30 -> Color(0xFFFFF3E0)  // 11-30 dias: Amarelo
        else -> Color(0xFFE8F5E9)                         // > 30 dias: Verde
    }
    val urgencyTextColor = when {
        product.expiresInDays <= 10 -> Color(0xFFD32F2F)
        product.expiresInDays <= 30 -> Color(0xFFF57C00)
        else -> Color(0xFF388E3C)
    }

    Card(
        onClick = onClick,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            
            // Image Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color(0xFFF9F9F9))
            ) {
                key(product.id){
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.urlImagem)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Desconto (Top Left)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(RedPrimary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "-${product.discountPercent}%",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Black)
                    )
                }

                // Fav (Top Right)
                IconButton(onClick = onToggleFavorite, modifier = Modifier.align(Alignment.TopEnd)) {
                    Icon(
                        imageVector = if (product.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = if (product.isFavorite) RedPrimary else Color.White,
                        modifier = Modifier.shadow(elevation = if (product.isFavorite) 0.dp else 4.dp, shape = CircleShape)
                    )
                }
            }

            // Central Highlight: Vencimento
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(urgencyBgColor)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Vence em ${product.expiresInDays} dias",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = urgencyTextColor
                    )
                )
            }

            // Info Area
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Box(modifier = Modifier.height(48.dp), contentAlignment = Alignment.TopStart) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 2,
                        minLines = 2, // fallback for < 1.4.0 is the Box height
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${product.storeName} • ${"%.1f".format(product.distanceKm)} km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "R$ ${"%.2f".format(product.priceNow)}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = RedPrimary)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "R$ ${"%.2f".format(product.priceOriginal)}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        ),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
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
