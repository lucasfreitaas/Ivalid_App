package com.example.ivalid_compose.domain.donation

enum class FidelityLevel(val minDonations: Int, val maxDonations: Int?, val cashbackMultiplier: Double, val label: String) {
    BRONZE(0, 10, 0.01, "Bronze (1%)"),
    PRATA(11, 30, 0.03, "Prata (3%)"),
    OURO(31, null, 0.05, "Ouro (5%)")
}

data class UserDonationStats(
    val totalDonations: Int,
    val pendingCashback: Double,
    val availableCashback: Double
)

class DonationGamificationService {

    /**
     * Define o nível do usuário baseado na quantidade de doações.
     */
    fun getLevelForDonationCount(count: Int): FidelityLevel {
        return when {
            count in FidelityLevel.BRONZE.minDonations..(FidelityLevel.BRONZE.maxDonations ?: Int.MAX_VALUE) -> FidelityLevel.BRONZE
            count in FidelityLevel.PRATA.minDonations..(FidelityLevel.PRATA.maxDonations ?: Int.MAX_VALUE) -> FidelityLevel.PRATA
            else -> FidelityLevel.OURO
        }
    }

    /**
     * Calcula o cashback ganho para uma determinada doação
     * baseado no nível atual do usuário.
     */
    fun calculateCashback(donationValue: Double, currentDonationCount: Int): Double {
        val level = getLevelForDonationCount(currentDonationCount)
        return donationValue * level.cashbackMultiplier
    }

    /**
     * Retorna quantas doações faltam para atingir o próximo nível,
     * ou null se o usuário já estiver no nível máximo.
     */
    fun getDonationsNeededForNextLevel(count: Int): Int? {
        val level = getLevelForDonationCount(count)
        return level.maxDonations?.let { max ->
            (max + 1) - count
        }
    }

    /**
     * Retorna o máximo desconto possível em uma compra (Trava de segurança de 15%).
     *
     * @param cartTotal O valor total da nova compra.
     * @param availableCashback O saldo de cashback disponível.
     */
    fun calculateMaxDiscountAllowed(cartTotal: Double, availableCashback: Double): Double {
        val maxAllowedByRule = cartTotal * 0.15
        return if (availableCashback > maxAllowedByRule) maxAllowedByRule else availableCashback
    }

    /**
     * Validação simples para ver se um crédito está expirado (Validade de 45 dias)
     */
    fun isCashbackCreditValid(createdAtTimestamp: Long, currentTimestamp: Long): Boolean {
        val fortyFiveDaysInMillis = 45L * 24 * 60 * 60 * 1000
        return (currentTimestamp - createdAtTimestamp) <= fortyFiveDaysInMillis
    }
}
