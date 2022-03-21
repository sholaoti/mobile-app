package network.etna.etnawallet.repository.wallets.cryptowallet

import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCToken

class CryptoWalletConfig(
    var wallets: List<CryptoWalletModel>
)

class CryptoWalletModel(
    val id: String,
    var name: String,
    var isActive: Boolean,
    val platformId: String?,
    val platformTokens: MutableMap<String, List<ERCToken>>
)
