package com.example.ivalid_compose.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ivalid_compose.ui.theme.RedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var locationEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RedPrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                "Preferências do Aplicativo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = RedPrimary
            )
            Spacer(Modifier.height(16.dp))

            Surface(shape = RoundedCornerShape(12.dp), tonalElevation = 2.dp) {
                Column {
                    SettingsSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "Gerenciar Notificações",
                        subtitle = "Promoções e alertas de validade",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    Divider()
                    SettingsSwitchItem(
                        icon = Icons.Default.LocationOn,
                        title = "Serviços de Localização",
                        subtitle = "Buscar lojar próximas de você",
                        checked = locationEnabled,
                        onCheckedChange = { locationEnabled = it }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                "Dados e Histórico",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = RedPrimary
            )
            Spacer(Modifier.height(16.dp))

            Surface(shape = RoundedCornerShape(12.dp), tonalElevation = 2.dp) {
                Column {
                    SettingsActionItem(
                        icon = Icons.Default.History,
                        title = "Limpar Histórico de Busca",
                        onClick = { /* TODO */ }
                    )
                    Divider()
                    SettingsActionItem(
                        icon = Icons.Default.DeleteForever,
                        title = "Excluir Minha Conta",
                        isDestructive = true,
                        onClick = { /* TODO */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray)
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = RedPrimary, checkedTrackColor = RedPrimary.copy(alpha=0.3f))
        )
    }
}

@Composable
fun SettingsActionItem(icon: ImageVector, title: String, isDestructive: Boolean = false, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (isDestructive) Color.Red else Color.Gray)
        Spacer(Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = if (isDestructive) Color.Red else Color.Black)
        Spacer(Modifier.weight(1f))
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}
