package network.etna.etnawallet.ui.asset

import network.etna.etnawallet.repository.wallets.etnawallet.AssetIdHolder

class AssetScenarioModel {
    lateinit var assetIdHolder: AssetIdHolder

    val platform: String
        get() { return assetIdHolder.platform }

    val walletAddress: String
        get() { return assetIdHolder.walletAddress }
}