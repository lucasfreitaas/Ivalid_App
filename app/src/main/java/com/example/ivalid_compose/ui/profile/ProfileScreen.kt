package com.example.ivalid_compose.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ivalid_compose.domain.donation.DonationGamificationService
import com.example.ivalid_compose.domain.donation.FidelityLevel
import com.example.ivalid_compose.ui.theme.AppTheme
import com.example.ivalid_compose.ui.theme.GreenAccent
import com.example.ivalid_compose.ui.theme.RedPrimary
import com.example.ivalid_compose.ui.theme.YellowAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val state = viewModel.uiState
    val user = state.userProfile
    
    val gamificationService = remember { DonationGamificationService() }
    val fidelityLevel = gamificationService.getLevelForDonationCount(state.totalDonations)
    val neededToNext = gamificationService.getDonationsNeededForNextLevel(state.totalDonations)

    var isPaymentDialogVisible by remember { mutableStateOf(false) }

    val levelColor = when (fidelityLevel) {
        FidelityLevel.BRONZE -> Color(0xFFCD7F32)   // Bronze/Cobre metálico
        FidelityLevel.PRATA  -> Color(0xFFC0C0C0)   // Prata/Cinza azulado
        FidelityLevel.OURO   -> Color(0xFFFFD700)   // Ouro/Dourado vibrante
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                RedPrimary.copy(alpha = 0.12f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Spacer(Modifier.height(40.dp)) // Safe area simulada
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(RedPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = RedPrimary
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (state.isLoading) "Carregando..." else user.name.ifEmpty { "Cliente Ivalid" },
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Assinante Premium Ivalid >",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = RedPrimary,
                            modifier = Modifier.clickable { /* open premium */ }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // --- CARDS & LIST ---
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                
                // Card "Ivalid Pago"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Ivalid", color = RedPrimary, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                            Text("Pago", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Gerencie suas formas de pagamento e saldos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { isPaymentDialogVisible = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.width(16.dp))
                            Text("Pagamentos", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // --- Card Fidelidade / Doações ---
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(1.dp, levelColor.copy(alpha=0.3f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.WorkspacePremium, contentDescription = null, tint = levelColor, modifier = Modifier.size(28.dp).shadow(2.dp, CircleShape, ambientColor = levelColor))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Nível ${fidelityLevel.label.substringBefore(" ")}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = levelColor)
                                Text("Multiplicador de ${fidelityLevel.label.substringAfter(" ")}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Cashback", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("R$ ${"%.2f".format(state.availableCashback)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = GreenAccent)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        if (neededToNext != null) {
                            val max = fidelityLevel.maxDonations ?: state.totalDonations
                            val totalOfLevelSpan = max - fidelityLevel.minDonations + 1
                            val currentProgress = (state.totalDonations - fidelityLevel.minDonations).toFloat() / totalOfLevelSpan.toFloat()

                            Text("Faltam $neededToNext doações para o próximo nível", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = currentProgress,
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
                                color = levelColor,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        } else {
                            Text("Você atingiu o nível máximo (Ouro)!", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = levelColor)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Menu Items
                ProfileMenuItem(icon = Icons.Outlined.ChatBubbleOutline, label = "Conversas", badgeCount = 1)
                ProfileMenuItem(icon = Icons.Outlined.Notifications, label = "Notificações", badgeCount = 3)
                ProfileMenuItem(icon = Icons.Outlined.PersonOutline, label = "Dados da conta")
                ProfileMenuItem(icon = Icons.Outlined.FavoriteBorder, label = "Favoritos")
                ProfileMenuItem(icon = Icons.Outlined.LocalActivity, label = "Cupons")
                ProfileMenuItem(
                    icon = Icons.Outlined.LocationOn, 
                    label = "Endereços", 
                    onClick = { viewModel.setAddressDialogVisible(true) }
                )
                
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                
                ProfileMenuItem(icon = Icons.Outlined.HelpOutline, label = "Ajuda")
                ProfileMenuItem(icon = Icons.Outlined.Settings, label = "Configurações")
                ProfileMenuItem(icon = Icons.Outlined.Security, label = "Segurança")
                
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp, 
                    label = "Sair da conta", 
                    iconTint = RedPrimary, 
                    labelColor = RedPrimary,
                    hideChevron = true,
                    onClick = { viewModel.logout(onLogout) }
                )
                
                Spacer(Modifier.height(40.dp)) // padding pro BottomNav
            }
        }
        
        // Error Snackbar se houver
        if (state.error != null) {
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ) {
                Text(state.error!!)
            }
        }
    }

    if (state.isAddressDialogVisible) {
        ModernAddressModal(
            state = state,
            onDismiss = { viewModel.setAddressDialogVisible(false) },
            onUpdateField = viewModel::updateAddressField
        )
    }

    if (isPaymentDialogVisible) {
        ModernPaymentModal(onDismiss = { isPaymentDialogVisible = false })
    }
}
@Composable
private fun ModernAddressModal(
    state: ProfileUiState,
    onDismiss: () -> Unit,
    onUpdateField: (AddressField, String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Meu Endereço", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state.addressData.cep,
                    onValueChange = { val clean = it.filter { char -> char.isDigit() }.take(8); onUpdateField(AddressField.CEP, clean) },
                    label = { Text("CEP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { if(state.isAddressLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = RedPrimary) },
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = state.addressData.street,
                    onValueChange = { onUpdateField(AddressField.STREET, it) },
                    label = { Text("Endereço") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.addressData.number,
                        onValueChange = { onUpdateField(AddressField.NUMBER, it) },
                        label = { Text("Número") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = state.addressData.complement,
                        onValueChange = { onUpdateField(AddressField.COMPLEMENT, it) },
                        label = { Text("Complemento") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                OutlinedTextField(
                    value = state.addressData.neighborhood,
                    onValueChange = { onUpdateField(AddressField.NEIGHBORHOOD, it) },
                    label = { Text("Bairro") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = state.addressData.city,
                    onValueChange = { onUpdateField(AddressField.CITY, it) },
                    label = { Text("Cidade") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss, 
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Endereço", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun ModernPaymentModal(onDismiss: () -> Unit) {
    var cardNumber by remember { mutableStateOf("") }
    var isValidNumber by remember { mutableStateOf(true) }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    // Luhn algorithm validation
    fun isValidLuhn(number: String): Boolean {
        if (number.length < 13) return false
        var sum = 0
        var alternate = false
        for (i in number.length - 1 downTo 0) {
            var n = number[i] - '0'
            if (alternate) {
                n *= 2
                if (n > 9) n = (n % 10) + 1
            }
            sum += n
            alternate = !alternate
        }
        return (sum % 10 == 0)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text("Adicionar Cartão", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = {
                        val filtered = it.filter { char -> char.isDigit() }.take(16)
                        cardNumber = filtered
                        isValidNumber = if (filtered.isEmpty()) true else isValidLuhn(filtered)
                    },
                    label = { Text("Número do Cartão") },
                    isError = !isValidNumber && cardNumber.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    supportingText = {
                        if (!isValidNumber && cardNumber.isNotEmpty()) Text("Número inválido (Luhn alg)")
                    }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = {
                            val filtered = it.filter { char -> char.isDigit() }.take(4)
                            expiryDate = filtered
                        },
                        label = { Text("Validade (MM/AA)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = {
                            val filtered = it.filter { char -> char.isDigit() }.take(4)
                            cvv = filtered
                        },
                        label = { Text("CVV") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (isValidNumber && cardNumber.length >= 13) onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                enabled = cardNumber.isNotEmpty() && isValidNumber
            ) {
                Text("Adicionar Cartão", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    )
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    badgeCount: Int = 0,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    hideChevron: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(26.dp))
        Spacer(Modifier.width(16.dp))
        Text(
            text = label, 
            style = MaterialTheme.typography.bodyLarge, 
            fontWeight = FontWeight.Medium,
            color = labelColor,
            modifier = Modifier.weight(1f)
        )
        
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(RedPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(Modifier.width(12.dp))
        }
        
        if (!hideChevron) {
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProfileScreen() {
    AppTheme {
        ProfileScreen(onLogout = {})
    }
}