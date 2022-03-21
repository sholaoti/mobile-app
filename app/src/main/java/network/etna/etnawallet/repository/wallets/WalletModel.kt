package network.etna.etnawallet.repository.wallets

import network.etna.etnawallet.sdk.network.Currency
import java.math.BigDecimal

data class CurrencyAmount(
    val amount: BigDecimal,
    val currency: Currency
) {
    operator fun plus(other: CurrencyAmount): CurrencyAmount {
        assert(currency == other.currency)
        return CurrencyAmount(
            amount + other.amount,
            currency
        )
    }
}

