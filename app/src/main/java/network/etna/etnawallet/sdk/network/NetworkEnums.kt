package network.etna.etnawallet.sdk.network

import network.etna.etnawallet.R

enum class Currency {
    USD,
    EUR
}

val Currency.icDrawableResource: Int
    get() {
        return when (this) {
            Currency.USD -> R.drawable.currency_usd
            Currency.EUR -> R.drawable.currency_eur
        }
    }

val Currency.symbol: String
    get() {
        return when (this) {
            Currency.USD -> "$"
            Currency.EUR -> "€"
        }
    }

