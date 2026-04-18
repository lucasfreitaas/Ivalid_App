package com.example.ivalid_compose.ui.donation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import android.content.Context
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ivalid_compose.ui.theme.RedPrimary
import com.example.ivalid_compose.ui.home.HomeViewModel
import com.example.ivalid_compose.ui.cart.CartViewModel
import com.example.ivalid_compose.ui.home.Product
import android.widget.Toast
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationScreen(
    viewModel: DonationViewModel = viewModel(),
    homeViewModel: HomeViewModel,
    cartViewModel: CartViewModel
) {
    val state = viewModel.uiState
    val products = homeViewModel.uiState.filteredProducts
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("IvalidPrefs", Context.MODE_PRIVATE)

    LaunchedEffect(Unit) {
        val hasSeenDialog = prefs.getBoolean("has_seen_donation_dialog", false)
        if (!hasSeenDialog) {
            viewModel.showExplainerDialog()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doações", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.showExplainerDialog() }) {
                        Icon(Icons.Default.Info, contentDescription = "Informações sobre doação")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = RedPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val context = LocalContext.current
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products) { item ->
                    DonationCard(
                        item = item,
                        onAdd = { 
                            cartViewModel.add(item, 1, isDonationContext = true)
                            Toast.makeText(context, "Produto doado enviado ao carrinho!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        if (state.explainerDialogVisible) {
            ExplanationDialog(onDismiss = { 
                prefs.edit().putBoolean("has_seen_donation_dialog", true).apply()
                viewModel.dismissExplainerDialog() 
            })
        }
    }
}

@Composable
fun DonationCard(item: Product, onAdd: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.urlImagem)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.height(48.dp), contentAlignment = Alignment.TopStart) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(14.dp), tint = RedPrimary)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Para ONGs parceiras",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "R$ %.2f".format(item.priceNow),
                style = MaterialTheme.typography.titleMedium,
                color = RedPrimary,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                Text("Doar Item", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun ExplanationDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                Text("Entendi, quero doar!", fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Apoie quem precisa",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "A entrega é feita diretamente para as ONGs parceiras que combatem a fome.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f))
                ) {
                    Text(
                        text = "Sua solidariedade vale descontos! Doe alimentos e ganhe cashback de até 5% sobre o valor doado para usar em suas próximas compras no Ivalid.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(12.dp),
                        color = RedPrimary
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}
