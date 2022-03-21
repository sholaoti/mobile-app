package network.etna.etnawallet.repository.coinrate

import network.etna.etnawallet.repository.wallets.etnawallet.AssetIdHolder
import network.etna.etnawallet.sdk.network.Currency

sealed class RateModel(
    val currency: Currency,
    var rate: Double,
    var change24h: Double
) {
    class Coin(
        val coin: String,
        currency: Currency,
        rate: Double,
        change24h: Double
    ): RateModel(currency, rate, change24h)

    class Token(
        val platform: String,
        val tokenAddress: String,
        currency: Currency,
        rate: Double,
        change24h: Double
    ): RateModel(currency, rate, change24h)
}

fun List<RateModel>.getCoinRateModel(coin: String): RateModel.Coin? {
    return this
        .mapNotNull { it as? RateModel.Coin }
        .firstOrNull { it.coin == coin }
}

fun List<RateModel>.getTokenRateModel(platform: String, tokenAddress: String): RateModel.Token? {
    return this
        .mapNotNull { it as? RateModel.Token }
        .firstOrNull { it.platform == platform && it.tokenAddress.equals(tokenAddress, true) }
}

fun List<RateModel>.getRateModel(assetIdHolder: AssetIdHolder): RateModel? {
    return this
        .firstOrNull {
            (assetIdHolder.isCurrency && it is RateModel.Coin && it.coin == assetIdHolder.referenceAssetId) ||
                    (!assetIdHolder.isCurrency && it is RateModel.Token && it.platform == assetIdHolder.platform && it.tokenAddress == assetIdHolder.assetId)
        }
}