package network.etna.etnawallet.repository.wallets.etnawallet

import network.etna.etnawallet.repository.wallets.CurrencyAmount
import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCToken

class EtnaWalletModel(
    val name: String,
    val id: String,
    val isActive: Boolean,
    val isMainnet: Boolean,
    val blockchains: List<EtnaBlockchainModel>,
    val currencyBalance: CurrencyAmount?,
    val currency24hChange: CurrencyAmount?
)

class EtnaBlockchainModel(
    val id: String,
    val coin: EtnaCoinModel,
    val tokens: List<EtnaTokenModel>,
    val address: String,
    val name: String?,
    val symbol: String?,
    val marketData: Boolean,
    val weight: Double
)

class EtnaCoinModel(
    val id: String,
    val referenceId: String,
    val balance: Double?,
    val change24h: Double?,
    val currencyBalance: CurrencyAmount?,
    val currency24hChange: CurrencyAmount?
)

class EtnaTokenModel(
    val token: ERCToken,
    val balance: Double?,
    val change24h: Double?,
    val currencyBalance: CurrencyAmount?,
    val currency24hChange: CurrencyAmount?
)

enum class BlockchainAssetType {
    CURRENCY, TOKEN
}

/***
 * get url by size
 */
typealias URLProvider = (Int) -> String

class BlockchainAsset(
    val id: String,
    val referenceId: String,
    val name: String,
    val platform: String,
    val address: String,
    val networkName: String?,
    val iconURLProvider: URLProvider,
    val type: BlockchainAssetType,
    val balance: Double?,
    val change24h: Double?,
    val currencyBalance: CurrencyAmount?,
    val currency24hChange: CurrencyAmount?
)
