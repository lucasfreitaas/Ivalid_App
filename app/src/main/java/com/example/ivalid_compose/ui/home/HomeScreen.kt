package com.example.ivalid_compose.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ivalid_compose.R
import com.example.ivalid_compose.ui.theme.AppTheme
import com.example.ivalid_compose.ui.theme.GreenAccent
import com.example.ivalid_compose.ui.theme.RedPrimary
import com.example.ivalid_compose.ui.theme.RedPrimaryDark
import com.example.ivalid_compose.ui.theme.YellowAccent
import kotlinx.coroutines.launch


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
                contentDescription = null
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                TextButton(onClick = onClear) { Text("Limpar") }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
            cursorColor = MaterialTheme.colorScheme.primary
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
        items(categories.size) { idx ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenProduct: (Product) -> Unit,
    onOpenNotifications: () -> Unit = {},
    onOpenFilters: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onSeeAll: () -> Unit = {},
    cartCount: Int = 0,
    onOpenCart: () -> Unit = {}
) {
    val state = viewModel.uiState

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.widthIn(min = 280.dp)
            ) {
                DrawerHeader(
                    userName = "Ricardo Gabriel",
                    onClickAvatar = { /* no-op */ }
                )
                Divider()

                NavigationDrawerItem(
                    label = { Text("Fale conosco") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Outlined.SupportAgent, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Ricardo",
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
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            ProfileAvatar(
                                modifier = Modifier.size(32.dp),
                                contentDescription = "Perfil"
                            )
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
                            IconButton(onClick = onOpenCart) {
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingCart,
                                    contentDescription = "Carrinho"
                                )
                            }
                        }

                        IconButton(onClick = onOpenNotifications) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notificações"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onOpenFilters, containerColor = RedPrimary) {
                    Icon(Icons.Outlined.FilterList, contentDescription = "Filtros", tint = Color.White)
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 0.dp)
                ) {
                    SearchBar(
                        query = state.query,
                        onQueryChange = viewModel::onQueryChange,
                        onClick = onOpenSearch,
                        onClear = { viewModel.onQueryChange("") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    CategoryChips(
                        categories = state.categories,
                        selectedId = state.selectedCategoryId ?: "all",
                        onSelect = { id -> viewModel.onSelectCategory(if (id == "all") null else id) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    OfferBanner(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Ofertas perto do vencimento",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            "Ver tudo",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = GreenAccent,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier.clickable { onSeeAll() }
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        items(state.filteredProducts, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onOpenProduct(product) },
                                onToggleFavorite = { viewModel.toggleFavorite(product.id) }
                            )
                        }
                    }
                }


                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Ofertas perto do vencimento",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        "Ver tudo",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = GreenAccent,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier.clickable { onSeeAll() }
                    )
                }

                Spacer(Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(state.filteredProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onOpenProduct(product) },
                            onToggleFavorite = { viewModel.toggleFavorite(product.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerHeader(
    userName: String,
    onClickAvatar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileAvatar(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onClickAvatar),
            contentDescription = "Foto do perfil"
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(userName, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Ver perfil", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ProfileAvatar(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val painter = safePainterResource(R.drawable.logo_ivalid)
    if (painter != null) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier.clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Icon(
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun safePainterResource(id: Int): Painter? {
    val context = LocalContext.current
    val exists = remember(id) {
        try {
            context.resources.getResourceName(id)
            true
        } catch (_: android.content.res.Resources.NotFoundException) {
            false
        }
    }
    return if (exists) painterResource(id) else null
}


@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClick: () -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar produto, marca ou loja") },
        leadingIcon = {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_search),
                contentDescription = null
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                TextButton(onClick = onClear) { Text("Limpar") }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CategoryChips(
    categories: List<Category>,
    selectedId: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(categories.size) { idx ->
            val c = categories[idx]
            val selected = (selectedId == (c.id))
            FilterChip(
                selected = selected,
                onClick = { onSelect(c.id) },
                label = { Text(c.name) },
                leadingIcon = null,
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = if (selected) RedPrimary.copy(alpha = 0.10f) else Color(0xFFF2F2F2),
                    labelColor = if (selected) RedPrimary else MaterialTheme.colorScheme.onSurface,
                    selectedContainerColor = RedPrimary.copy(alpha = 0.18f),
                    selectedLabelColor = RedPrimary,
                    selectedLeadingIconColor = RedPrimary
                )
            )
        }
    }
}

@Composable
private fun OfferBanner() {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
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
    onToggleFavorite: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Card(
        onClick = onClick,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 0.dp, shape = shape)
    ) {
        Column(Modifier.padding(10.dp)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    contentScale = ContentScale.Fit
                )

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
                    product.expiresInDays <= 2 -> RedPrimary.copy(alpha = 0.15f) to RedPrimary
                    product.expiresInDays <= 7 -> YellowAccent.copy(alpha = 0.18f) to YellowAccent
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

            Spacer(Modifier.height(10.dp))

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
            onOpenProduct = {}
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
            onOpenProduct = {}
        )
    }
}