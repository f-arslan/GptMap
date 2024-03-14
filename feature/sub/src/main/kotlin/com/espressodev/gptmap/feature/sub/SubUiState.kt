package com.espressodev.gptmap.feature.sub

data class SubUiState(
    val selectedCard: CardType = CardType.Annual,
    val monthlyPrice: Double = 4.99,
    val annualPrice: Double = 34.99
)

enum class CardType {
    Monthly, Annual
}
