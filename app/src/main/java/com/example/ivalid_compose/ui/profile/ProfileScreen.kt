package com.example.ivalid_compose.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ivalid_compose.ui.theme.AppTheme
import com.example.ivalid_compose.ui.theme.RedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onLogout: () -> Unit // Callback para navegação (irá para a tela de login)
) {
    val state = viewModel.uiState
    val user = state.userProfile

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RedPrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. Seção do Avatar/Nome ---
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(RedPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Ícone de Perfil",
                    modifier = Modifier.size(64.dp),
                    tint = RedPrimary
                )
            }
            Spacer(Modifier.height(16.dp))

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    user.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Membro desde Outubro/2025",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(Modifier.height(32.dp))

            // --- 2. Informações de Contato (Cartão) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileItem(Icons.Default.Person, "Nome", user.name)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ProfileItem(Icons.Default.Email, "Email", user.email)

                    if (user.isVerified) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("Conta Verificada", color = Color(0xFF4CAF50), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = { viewModel.setAddressDialogVisible(true) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, RedPrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedPrimary)
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Adicionar Endereço", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(32.dp))

            // --- 3. Botão de Logout ---
            Button(
                onClick = { viewModel.logout(onLogout) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Sair da Conta")
            }

            if (state.error != null) {
                Text(state.error!!, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }

    if (state.isAddressDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.setAddressDialogVisible(false) },
            confirmButton = {
                Button(onClick = { viewModel.setAddressDialogVisible(false) }, colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)) {
                    Text("Salvar Endereço")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setAddressDialogVisible(false) }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            title = { Text("Adicionar Endereço", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = state.addressData.cep,
                        onValueChange = { val clean = it.filter { char -> char.isDigit() }.take(8); viewModel.updateAddressField(AddressField.CEP, clean) },
                        label = { Text("CEP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { if(state.isAddressLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = RedPrimary) }
                    )
                    OutlinedTextField(
                        value = state.addressData.street,
                        onValueChange = { viewModel.updateAddressField(AddressField.STREET, it) },
                        label = { Text("Endereço") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = state.addressData.number,
                            onValueChange = { viewModel.updateAddressField(AddressField.NUMBER, it) },
                            label = { Text("Número") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = state.addressData.complement,
                            onValueChange = { viewModel.updateAddressField(AddressField.COMPLEMENT, it) },
                            label = { Text("Complemento") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = state.addressData.neighborhood,
                        onValueChange = { viewModel.updateAddressField(AddressField.NEIGHBORHOOD, it) },
                        label = { Text("Bairro") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.addressData.city,
                        onValueChange = { viewModel.updateAddressField(AddressField.CITY, it) },
                        label = { Text("Cidade") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        )
    }
}
@Composable
fun ProfileItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
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