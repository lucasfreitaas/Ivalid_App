package com.example.ivalid_compose.ui.checkout

import android.graphics.Color as BitmapColor
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ivalid_compose.ui.theme.AppTheme
import com.example.ivalid_compose.ui.theme.GreenAccent
import com.example.ivalid_compose.ui.theme.RedPrimary
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderConfirmationScreen(
    orderId: String,
    totalValue: Double,
    onBackToHome: () -> Unit
) {
    val pixCodeMock = "00020126360014BR.GOV.BCB.PIX01149912345678901234520400005303986540510.505802BR5913NomeEmpresa6007BRASIL62070503***6304CA11"
    val clipboardManager = LocalClipboardManager.current

    Scaffold (
        topBar = {
            TopAppBar(
                title = {Text("Finalizar Pagamento", color = Color.Black)},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RedPrimary)
            )
        },
        bottomBar = {
            Button(
                onClick = onBackToHome,
                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Voltar para início", fontSize = MaterialTheme.typography.titleMedium.fontSize)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Pedido #$orderId",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Valor Total: R$ %.2f".format(totalValue),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(32.dp))

            // --- QR CODE (Mockado) ---
            Text("Escaneie para pagar com PIX", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            QrCodeImage(content = pixCodeMock, size = 200)

            Spacer(Modifier.height(32.dp))

            // --- CÓDIGO PIX COPIA E COLA ---
            Text("Ou use o código Copia e Cola", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GreenAccent, RoundedCornerShape(12.dp))
                    .clickable {
                        clipboardManager.setText(AnnotatedString(pixCodeMock))
                        // Aqui você mostraria uma Snackbar ou Toast "Código copiado!"
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pixCodeMock.take(30) + "...", // Exibir apenas uma prévia
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Filled.ContentCopy, contentDescription = "Copiar Código", tint = GreenAccent)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Seu pedido será confirmado após o pagamento.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun QrCodeImage(content: String, size: Int = 200){
    val bitmap = remember(content){
        try{
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size
            )

            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            for(y in 0 until height) {
                val offset = y * width
                for (x in 0 until width){
                    pixels[offset + x] = if (bitMatrix.get(x, y)) BitmapColor.BLACK else BitmapColor.WHITE
                }
            }

            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        } catch (e: WriterException){
            e.printStackTrace()
            null
        }
    }

    if(bitmap != null){
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "QR Code PIX",
            modifier = Modifier
                .size(size.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
        )
    } else {
        Text("Erro ao gerar QR Code", color = RedPrimary)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOrderConfirmationScreen() {
    AppTheme {
        OrderConfirmationScreen(
            orderId = "ABC-123",
            totalValue = 150.99,
            onBackToHome = {}
        )
    }
}