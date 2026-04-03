package com.example.ivalid_compose.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

object MercadoPagoService {

    private const val TAG = "MercadoPagoService"

    // Seu Access Token de TESTE do Mercado Pago
    private const val ACCESS_TOKEN = "TEST-8818899977651418-040317-45c98aa7f7d2247a9e71a36759efefc0-2964501635"

    private const val BASE_URL = "https://api.mercadopago.com"

    /**
     * Gera uma cobrança PIX via Mercado Pago.
     * Retorna a String do QR Code (copia e cola) ou null em caso de falha.
     */
    suspend fun gerarPix(valor: Double, txid: String): String? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Iniciando geração PIX - Valor: $valor | TxID: $txid")

            val url = URL("$BASE_URL/v1/payments")
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $ACCESS_TOKEN")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("X-Idempotency-Key", UUID.randomUUID().toString())
                doOutput = true
                connectTimeout = 30000
                readTimeout = 30000
            }

            // Arredonda para 2 casas decimais — o MP NÃO aceita imprecisão de float
            // .toDouble() garante serialização como número JSON, não como String
            val valorArredondado = BigDecimal(valor).setScale(2, RoundingMode.HALF_UP).toDouble()

            val jsonBody = JSONObject().apply {
                put("transaction_amount", valorArredondado)
                put("description", "Pedido Ivalid - $txid")
                put("payment_method_id", "pix")
                put("payer", JSONObject().apply {
                    // Mínimo obrigatório para Sandbox PIX é apenas um e-mail válido.
                    // Muitas vezes CPFs falsos disparam Erro 500 interno na API do MP.
                    put("email", "comprador.teste.sandbox@gmail.com") 
                })
            }

            Log.d(TAG, "Body enviado: ${jsonBody.toString(2)}")

            connection.outputStream.bufferedWriter().use { it.write(jsonBody.toString()) }

            val statusCode = connection.responseCode
            Log.d(TAG, "Status HTTP: $statusCode")

            if (statusCode in 200..201) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d(TAG, "Resposta OK: $response")
                return@withContext parsePixQrCode(response)
            } else {
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "sem corpo de erro"
                Log.e(TAG, "Erro HTTP $statusCode: $errorBody")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Exceção ao gerar PIX", e)
        }

        return@withContext null
    }

    private fun parsePixQrCode(jsonString: String): String? {
        return try {
            val root = JSONObject(jsonString)
            val poi = root.optJSONObject("point_of_interaction")
            val txData = poi?.optJSONObject("transaction_data")
            val qrCode = txData?.optString("qr_code")

            if (qrCode.isNullOrEmpty()) {
                Log.w(TAG, "qr_code vazio na resposta. Verifique point_of_interaction: $poi")
                null
            } else {
                Log.d(TAG, "QR Code obtido com sucesso (${qrCode.length} chars)")
                qrCode
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao parsear resposta PIX", e)
            null
        }
    }
}
