package network.etna.etnawallet.sdk.api.etna.model

import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCTokenType

class BlockchainPlatformModel(
    val id: String,
    val name: String,
    val symbol: String,
    val weight: Float,
    val mainNet: Boolean,
    val hdWalletPath: String,
    val supportedTokens: List<ERCTokenType>
) {
    companion object {
        private const val HARDENED_BIT: Int = 0x80000000.toInt()
    }

    fun getWalletPath(): IntArray {
        var hd = hdWalletPath
//        if (id == "ethereum-testnet") {
//            hd = "m / 44' / 60' / 0' / 0 / 0"
//        }

        val path = hd.replace(" ","")
        return path.split("/").drop(1).map {
            var str = it
            val isHardened = str.contains("'")
            str = str.replace("'", "")
            if (isHardened) {
                str.toInt() or HARDENED_BIT
            } else {
                str.toInt()
            }
        }.toIntArray()
    }
}